package com.truongquoctoan.example01.service.impl;

import com.truongquoctoan.example01.controller.OrderWebSocketController;
import com.truongquoctoan.example01.dto.OrderDto;
import com.truongquoctoan.example01.entity.CoffeeTable;
import com.truongquoctoan.example01.entity.Order;
import com.truongquoctoan.example01.entity.Promotion;
import com.truongquoctoan.example01.entity.User;
import com.truongquoctoan.example01.repository.CoffeeTableRepository;
import com.truongquoctoan.example01.repository.OrderRepository;
import com.truongquoctoan.example01.repository.PromotionRepository;
import com.truongquoctoan.example01.repository.UserRepository;
import com.truongquoctoan.example01.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CoffeeTableRepository coffeeTableRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final OrderWebSocketController webSocketController;

    // =========================================================
    // LẤY TẤT CẢ ĐƠN HÀNG
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // =========================================================
    // LẤY ĐƠN HÀNG THEO ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        if (id == null) {
            throw new RuntimeException(
                    "❌ ID đơn hàng không được để trống"
            );
        }

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy đơn hàng có id: " + id
                        )
                );
    }

    // =========================================================
    // LẤY ĐƠN ĐANG MỞ CỦA BÀN
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Order getActiveOrderByTableId(Long tableId) {
        if (tableId == null) {
            throw new RuntimeException(
                    "❌ tableId không được để trống"
            );
        }

        List<Order.Status> closedStatuses = List.of(
                Order.Status.PAID,
                Order.Status.CANCELLED
        );

        return orderRepository
                .findFirstByTable_IdAndStatusNotInOrderByIdDesc(
                        tableId,
                        closedStatuses
                )
                .orElse(null);
    }

    // =========================================================
    // TẠO ĐƠN HÀNG
    // =========================================================

    @Override
    @Transactional
    public Order createOrder(
            OrderDto dto,
            String username
    ) {
        validateCreateOrderRequest(dto, username);

        User creator = findUserByUsername(username);

        if (Boolean.FALSE.equals(creator.getIsActive())) {
            throw new RuntimeException(
                    "❌ Tài khoản đang bị khóa"
            );
        }

        CoffeeTable table = findTableById(
                dto.getTableId()
        );

        Promotion promotion = findPromotionOrNull(
                dto.getPromotionId()
        );

        User employee = null;
        User customer = null;

        boolean createdByCustomer =
                creator.getRole() == User.Role.USER;

        /*
         * ADMIN, EMPLOYEE và STAFF tạo đơn tại POS.
         */
        if (creator.getRole() == User.Role.ADMIN
                || creator.getRole() == User.Role.EMPLOYEE
                || creator.getRole() == User.Role.STAFF) {

            employee = creator;

            /*
             * Khi nhân viên chọn một khách hàng thành viên,
             * frontend phải gửi userId.
             *
             * Nếu không gửi userId thì đơn được xem là khách lẻ.
             */
            if (dto.getUserId() != null) {
                customer = findValidCustomer(
                        dto.getUserId()
                );
            }

        } else if (createdByCustomer) {

            /*
             * Khách đăng nhập frontend tự đặt món.
             *
             * Không lấy userId từ request vì người dùng có thể
             * sửa payload để gán đơn cho tài khoản khác.
             *
             * Luôn sử dụng tài khoản lấy từ JWT.
             */
            customer = creator;

        } else {
            throw new RuntimeException(
                    "❌ Vai trò tài khoản không được phép tạo đơn"
            );
        }

        /*
         * Khách hàng chỉ được tạo đơn PENDING.
         * Không cho khách tự gửi PAID từ frontend.
         */
        Order.Status resolvedStatus;

        if (createdByCustomer) {
            resolvedStatus = Order.Status.PENDING;
        } else {
            resolvedStatus = parseStatus(
                    dto.getStatus(),
                    Order.Status.PENDING
            );
        }

        BigDecimal totalAmount = normalizeAmount(
                dto.getTotalAmount()
        );

        String resolvedCustomerName =
                resolveCustomerName(
                        dto.getCustomerName(),
                        customer
                );

        Order order = Order.builder()
                .table(table)

                // Người tạo đơn tại POS
                .employee(employee)

                // Khách hàng đã đăng nhập hoặc khách được nhân viên chọn
                .user(customer)

                .promotion(promotion)
                .customerName(resolvedCustomerName)
                .status(resolvedStatus)
                .notes(normalizeText(dto.getNotes()))
                .totalAmount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        logCreatedOrder(
                savedOrder,
                creator
        );

        notifyNewOrder(savedOrder);

        return savedOrder;
    }

    // =========================================================
    // CẬP NHẬT ĐƠN HÀNG
    // =========================================================

    @Override
    @Transactional
    public Order updateOrder(
            Long id,
            OrderDto dto
    ) {
        if (dto == null) {
            throw new RuntimeException(
                    "❌ Dữ liệu cập nhật đơn hàng không hợp lệ"
            );
        }

        Order order = getOrderById(id);

        /*
         * StaffPage thanh toán chỉ gửi:
         *
         * {
         *   "status": "PAID",
         *   "totalAmount": ...,
         *   "customerName": ...
         * }
         *
         * Vì không có tableId nên bàn hiện tại được giữ nguyên.
         */
        if (dto.getTableId() != null) {
            CoffeeTable table = findTableById(
                    dto.getTableId()
            );

            order.setTable(table);
        }

        /*
         * Nhân viên có thể liên kết đơn khách lẻ với
         * một tài khoản khách hàng bằng userId.
         */
        if (dto.getUserId() != null) {
            User customer = findValidCustomer(
                    dto.getUserId()
            );

            order.setUser(customer);

            /*
             * Sau khi đã liên kết tài khoản,
             * tên khách được lấy từ tài khoản đó.
             */
            order.setCustomerName(
                    resolveCustomerName(
                            dto.getCustomerName(),
                            customer
                    )
            );
        } else if (dto.getCustomerName() != null) {

            /*
             * Không gửi userId:
             * giữ nguyên orders.user_id hiện tại.
             *
             * Đây là phần quan trọng khi StaffPage thanh toán
             * đơn do khách đăng nhập frontend tạo.
             */
            order.setCustomerName(
                    resolveCustomerName(
                            dto.getCustomerName(),
                            order.getUser()
                    )
            );
        }

        if (dto.getNotes() != null) {
            order.setNotes(
                    normalizeText(dto.getNotes())
            );
        }

        if (dto.getStatus() != null
                && !dto.getStatus().isBlank()) {

            Order.Status newStatus = parseStatus(
                    dto.getStatus(),
                    order.getStatus()
            );

            validateStatusTransition(
                    order.getStatus(),
                    newStatus
            );

            order.setStatus(newStatus);
        }

        if (dto.getTotalAmount() != null) {
            order.setTotalAmount(
                    normalizeAmount(
                            dto.getTotalAmount()
                    )
            );
        }

        if (dto.getPromotionId() != null) {
            Promotion promotion =
                    findPromotionOrNull(
                            dto.getPromotionId()
                    );

            order.setPromotion(promotion);
        }

        Order updatedOrder =
                orderRepository.save(order);

        logUpdatedOrder(updatedOrder);

        notifyOrderUpdate(updatedOrder);

        return updatedOrder;
    }

    // =========================================================
    // XÓA ĐƠN HÀNG
    // =========================================================

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (id == null) {
            throw new RuntimeException(
                    "❌ ID đơn hàng không được để trống"
            );
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy đơn hàng có id: " + id
                        )
                );

        if (order.getStatus() == Order.Status.PAID) {
            throw new RuntimeException(
                    "❌ Không thể xóa đơn hàng đã thanh toán"
            );
        }

        orderRepository.delete(order);
    }

    // =========================================================
    // VALIDATE REQUEST TẠO ĐƠN
    // =========================================================

    private void validateCreateOrderRequest(
            OrderDto dto,
            String username
    ) {
        if (dto == null) {
            throw new RuntimeException(
                    "❌ Dữ liệu tạo đơn không hợp lệ"
            );
        }

        if (username == null
                || username.isBlank()) {

            throw new RuntimeException(
                    "❌ Không xác định được người tạo đơn"
            );
        }

        /*
         * Website của bạn bán hàng tại quán nên mỗi đơn cần bàn.
         */
        if (dto.getTableId() == null) {
            throw new RuntimeException(
                    "❌ tableId không được để trống"
            );
        }
    }

    // =========================================================
    // TÌM USER THEO USERNAME
    // =========================================================

    private User findUserByUsername(
            String username
    ) {
        return userRepository
                .findByUsername(username.trim())
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy tài khoản: "
                                        + username
                        )
                );
    }

    // =========================================================
    // TÌM BÀN
    // =========================================================

    private CoffeeTable findTableById(
            Long tableId
    ) {
        return coffeeTableRepository
                .findById(tableId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy bàn có id: "
                                        + tableId
                        )
                );
    }

    // =========================================================
    // TÌM KHUYẾN MÃI
    // =========================================================

    private Promotion findPromotionOrNull(
            Long promotionId
    ) {
        if (promotionId == null) {
            return null;
        }

        return promotionRepository
                .findById(promotionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy khuyến mãi có id: "
                                        + promotionId
                        )
                );
    }

    // =========================================================
    // KIỂM TRA KHÁCH HÀNG
    // =========================================================

    private User findValidCustomer(
            Long userId
    ) {
        User customer = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "❌ Không tìm thấy khách hàng có id: "
                                        + userId
                        )
                );

        if (customer.getRole() != User.Role.USER) {
            throw new RuntimeException(
                    "❌ Tài khoản được chọn không phải khách hàng"
            );
        }

        if (Boolean.FALSE.equals(
                customer.getIsActive()
        )) {
            throw new RuntimeException(
                    "❌ Tài khoản khách hàng đang bị khóa"
            );
        }

        return customer;
    }

    // =========================================================
    // XỬ LÝ TÊN KHÁCH HÀNG
    // =========================================================

    private String resolveCustomerName(
            String requestedName,
            User customer
    ) {
        /*
         * Nếu đơn đã gắn tài khoản USER,
         * luôn ưu tiên fullName từ database.
         *
         * Vì vậy StaffPage gửi "Khách lẻ"
         * cũng không ghi đè tên tài khoản thật.
         */
        if (customer != null) {
            if (customer.getFullName() != null
                    && !customer.getFullName().isBlank()) {

                return customer
                        .getFullName()
                        .trim();
            }

            return customer.getUsername();
        }

        if (requestedName == null
                || requestedName.isBlank()) {

            return "Khách lẻ";
        }

        return requestedName.trim();
    }

    // =========================================================
    // XỬ LÝ TỔNG TIỀN
    // =========================================================

    private BigDecimal normalizeAmount(
            BigDecimal amount
    ) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(
                    "❌ Tổng tiền không được nhỏ hơn 0"
            );
        }

        return amount;
    }

    // =========================================================
    // XỬ LÝ TEXT
    // =========================================================

    private String normalizeText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    // =========================================================
    // XỬ LÝ TRẠNG THÁI
    // =========================================================

    private Order.Status parseStatus(
            String status,
            Order.Status defaultStatus
    ) {
        if (status == null
                || status.isBlank()) {

            return defaultStatus;
        }

        try {
            return Order.Status.valueOf(
                    status
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(
                    "❌ Trạng thái đơn hàng không hợp lệ: "
                            + status
            );
        }
    }

    private void validateStatusTransition(
            Order.Status currentStatus,
            Order.Status newStatus
    ) {
        if (currentStatus == null
                || newStatus == null) {

            return;
        }

        /*
         * Không cho đổi một đơn PAID về trạng thái đang xử lý.
         */
        if (currentStatus == Order.Status.PAID
                && newStatus != Order.Status.PAID) {

            throw new RuntimeException(
                    "❌ Không thể thay đổi trạng thái của đơn đã thanh toán"
            );
        }

        /*
         * Không cho đổi đơn đã hủy thành PAID.
         */
        if (currentStatus == Order.Status.CANCELLED
                && newStatus == Order.Status.PAID) {

            throw new RuntimeException(
                    "❌ Không thể thanh toán đơn hàng đã hủy"
            );
        }
    }

    // =========================================================
    // WEBSOCKET
    // =========================================================

    private void notifyNewOrder(
            Order order
    ) {
        try {
            webSocketController.notifyNewOrder(order);
        } catch (Exception exception) {
            log.warn(
                    "Không gửi được WebSocket tạo đơn: {}",
                    exception.getMessage()
            );
        }
    }

    private void notifyOrderUpdate(
            Order order
    ) {
        try {
            webSocketController.notifyOrderUpdate(order);
        } catch (Exception exception) {
            log.warn(
                    "Không gửi được WebSocket cập nhật đơn: {}",
                    exception.getMessage()
            );
        }
    }

    // =========================================================
    // LOG
    // =========================================================

    private void logCreatedOrder(
            Order order,
            User creator
    ) {
        log.info("======================================");
        log.info("ĐÃ TẠO ĐƠN HÀNG");
        log.info("Order ID: {}", order.getId());
        log.info("Người tạo: {}", creator.getUsername());
        log.info("Role người tạo: {}", creator.getRole());
        log.info("Status: {}", order.getStatus());
        log.info("Total amount: {}", order.getTotalAmount());

        if (order.getUser() != null) {
            log.info(
                    "Customer ID: {}",
                    order.getUser().getId()
            );

            log.info(
                    "Customer username: {}",
                    order.getUser().getUsername()
            );
        } else {
            log.info(
                    "Customer: NULL - khách lẻ"
            );
        }

        log.info("======================================");
    }

    private void logUpdatedOrder(
            Order order
    ) {
        log.info("======================================");
        log.info("ĐÃ CẬP NHẬT ĐƠN HÀNG");
        log.info("Order ID: {}", order.getId());
        log.info("Status: {}", order.getStatus());
        log.info("Total amount: {}", order.getTotalAmount());

        if (order.getUser() != null) {
            log.info(
                    "Customer ID: {}",
                    order.getUser().getId()
            );

            log.info(
                    "Customer username: {}",
                    order.getUser().getUsername()
            );
        } else {
            log.info(
                    "Customer: NULL - khách lẻ"
            );
        }

        log.info("======================================");
    }
}