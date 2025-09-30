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
