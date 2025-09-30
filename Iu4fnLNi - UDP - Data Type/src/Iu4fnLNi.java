import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Iu4fnLNi {
    public static void main(String[] args) throws Exception {
        final String SERVER_HOST = "203.162.10.109";
        final int SERVER_PORT = 2207;
        final String studentCode = "B22DCVT090";   // <-- sửa mã SV của bạn
        final String qCode = "Iu4fnLNi";

        // a) Gửi ";studentCode;qCode"
        String hello = ";" + studentCode + ";" + qCode;
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(7000);
        byte[] send = hello.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(send, send.length,
                InetAddress.getByName(SERVER_HOST), SERVER_PORT));
        System.out.println("[SENT] " + hello);

        // b) Nhận "requestId;a;b"  (một số đề có thể dùng dấu ',' giữa a và b -> xử lý cả 2)
        byte[] buf = new byte[65535];
        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        socket.receive(pkt);
        String resp = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8).trim();
        System.out.println("[RECV] " + resp);

        // Chuẩn hóa: thay ',' thành ';' rồi tách 3 phần: requestId, a, b
        String norm = resp.replace(',', ';');
        String[] parts = norm.split(";", 3);
        if (parts.length < 3) {
            System.out.println("Sai định dạng. Mong đợi 'requestId;a;b'.");
            socket.close();
            return;
        }
        String requestId = parts[0].trim();
        BigInteger a = new BigInteger(parts[1].trim());
        BigInteger b = new BigInteger(parts[2].trim());

        // c) Tính tổng và hiệu (a - b), gửi "requestId;sum,difference"
        BigInteger sum = a.add(b);
        BigInteger diff = a.subtract(b);
        String answer = requestId + ";" + sum.toString() + "," + diff.toString();

        byte[] ans = answer.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(ans, ans.length,
                InetAddress.getByName(SERVER_HOST), SERVER_PORT));
        System.out.println("[SENT] " + answer);

        // d) Đóng socket
        socket.close();
    }
}
