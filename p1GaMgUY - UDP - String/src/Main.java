import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        final String SERVER_HOST = "203.162.10.109";
        final int SERVER_PORT = 2208;
        final String STUDENT_CODE = "B22DCVT090";   // <-- sửa mã SV của bạn
        final String Q_CODE = "p1GaMgUY";

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(7000);

            // a) Gửi ";studentCode;qCode"
            String hello = ";" + STUDENT_CODE + ";" + Q_CODE;
            byte[] helloBytes = hello.getBytes(StandardCharsets.UTF_8);
            DatagramPacket helloPacket = new DatagramPacket(
                    helloBytes, helloBytes.length,
                    InetAddress.getByName(SERVER_HOST), SERVER_PORT);
            socket.send(helloPacket);
            System.out.println("[SENT ] " + hello);

            // b) Nhận "requestId;data"
            byte[] buf = new byte[65535];
            DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
            socket.receive(recvPacket);
            String resp = new String(
                    recvPacket.getData(), 0, recvPacket.getLength(),
                    StandardCharsets.UTF_8).trim();
            System.out.println("[RECV ] " + resp);

            String[] parts = resp.split(";", 2);
            if (parts.length < 2) {
                System.out.println("Phản hồi sai định dạng.");
                return;
            }
            String requestId = parts[0];
            String data = parts[1];

            // c) Sắp xếp từ theo thứ tự từ điển ngược (KHÔNG phân biệt hoa/thường)
            String[] words = data.trim().split("\\s+");
            Arrays.sort(words, String.CASE_INSENSITIVE_ORDER.reversed());
            String joined = String.join(",", words);

            String answer = requestId + ";" + joined;
            
            byte[] ansBytes = answer.getBytes(StandardCharsets.UTF_8);
            DatagramPacket ansPacket = new DatagramPacket(
                    ansBytes, ansBytes.length,
                    InetAddress.getByName(SERVER_HOST), SERVER_PORT);
            socket.send(ansPacket);
            System.out.println("[SENT ] " + answer);

            // (tùy chọn) nhận phản hồi xác nhận
            try {
                DatagramPacket confirmPkt = new DatagramPacket(new byte[4096], 4096);
                socket.receive(confirmPkt);
                String confirm = new String(confirmPkt.getData(), 0, confirmPkt.getLength(),
                        StandardCharsets.UTF_8).trim();
                System.out.println("[CONF ] " + confirm);
            } catch (SocketTimeoutException e) {
                // không có phản hồi thêm, không sao
            }

        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Hoàn tất. Đóng socket.");
            }
        }
    }
}
