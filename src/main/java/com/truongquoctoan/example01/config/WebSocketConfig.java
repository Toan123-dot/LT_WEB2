package com.truongquoctoan.example01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Đánh dấu lớp này là một lớp cấu hình (Configuration) của Spring Boot
@EnableWebSocketMessageBroker // Kích hoạt tính năng Broker xử lý tin nhắn WebSocket trong ứng dụng
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override   
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. Định nghĩa các kênh (Prefix) để Server gửi dữ liệu xuống Client Frontend
        // Client sẽ Đăng ký (Subscribe) các đường dẫn bắt đầu bằng "/topic" để lắng nghe dữ liệu real-time
        // Ví dụ: /topic/orders (thông báo có đơn hàng mới cho cả hệ thống công khai)
        config.enableSimpleBroker("/topic");
        
        // 2. Định nghĩa tiền tố đường dẫn khi Client Frontend muốn gửi tin nhắn LÊN Server
        // Khi dùng gói 'stompjs' ở frontend gửi tin, đường dẫn gọi lên bắt buộc phải có tiền tố này
        // Ví dụ: Client gửi dữ liệu đến "/app/create-order" -> Server sẽ hứng ở hàm @MessageMapping("/create-order")
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký cổng kết nối (Endpoint) WebSocket ban đầu cho hệ thống
        // Client Frontend sẽ dùng thư viện 'sockjs-client' để kết nối vào địa chỉ: http://localhost:8080/ws
        registry.addEndpoint("/ws")
                
                // CẤU HÌNH BẢO MẬT CORS: Cho phép domain của Frontend được quyền kết nối vào WebSocket
                // ⚠️ LƯU Ý QUAN TRỌNG: Bạn đang để cổng 3000 (React cũ), lát nữa cần đổi sang 5173 của Vite!
                .setAllowedOrigins("http://localhost:3000") 
                
                // Kích hoạt SockJS làm giải pháp dự phòng (Fallback) phòng trường hợp 
                // Trình duyệt cũ của khách hàng không hỗ trợ giao thức WebSocket thuần (Native WebSocket)
                .withSockJS();
    }
}