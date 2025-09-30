// [Mã câu hỏi (qCode): VRwOisD9].  Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. 
//Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản dưới đây:
// a.	Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng “;studentCode;qCode”. Ví dụ: “;B15DCCN001;5B35BCC1”
// b.	Nhận thông điệp từ server theo định dạng “requestId;data” 
// -	requestId là một chuỗi ngẫu nhiên duy nhất
// -	data là chuỗi dữ liệu cần xử lý
// c.	Xử lý chuẩn hóa chuỗi đã nhận thành theo nguyên tắc 
// i.	Ký tự đầu tiên của từng từ trong chuỗi là in hoa
// ii.	Các ký tự còn lại của chuỗi là in thường
// Gửi thông điệp chứa chuỗi đã được chuẩn hóa lên server theo định dạng “requestId;data”
// d.	Đóng socket và kết thúc chương trình

import java.net.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        final String SERVER_HOST = "203.162.10.109";
        final int SERVER_PORT = 2208;
        final String studentCode = "B22DCVT090";   // <-- sửa mã SV
        final String qCode = "VRwOisD9";

        // a) Gửi ";studentCode;qCode"
        String hello = ";" + studentCode + ";" + qCode;
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(7000);
        byte[] send = hello.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(send, send.length,
                InetAddress.getByName(SERVER_HOST), SERVER_PORT));
        System.out.println("[SENT] " + hello);

        // b) Nhận "requestId;data"
        byte[] buf = new byte[65535];
        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        socket.receive(pkt);
        String resp = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8).trim();
        System.out.println("[RECV] " + resp);

        String[] parts = resp.split(";", 2);
        String requestId = parts[0];
        String data = parts[1];

        // c) Chuẩn hoá: ký tự đầu của mỗi từ in hoa, còn lại in thường
        String norm = normalize(data);

        String answer = requestId + ";" + norm;
        byte[] ans = answer.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(ans, ans.length,
                InetAddress.getByName(SERVER_HOST), SERVER_PORT));
        System.out.println("[SENT] " + answer);

        // d) Đóng socket
        socket.close();
    }

    private static String normalize(String input) {
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i].toLowerCase();
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1) sb.append(w.substring(1));
            }
            if (i < words.length - 1) sb.append(" ");
        }
        return sb.toString();
    }
}
