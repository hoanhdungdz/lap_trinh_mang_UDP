import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RJ26zTAt {
    public static void main(String[] args) throws Exception {
        String serverHost = "203.162.10.109";
        int serverPort = 2207;

        // a) Gửi thông điệp ;studentCode;qCode
        String studentCode = "B22DCVT090";   // đổi thành mã SV của bạn
        String qCode = "RJ26zTAt";
        String hello =";" + studentCode + ";" + qCode;

        DatagramSocket socket = new DatagramSocket();
        byte[] sendData = hello.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(sendData, sendData.length,
                InetAddress.getByName(serverHost), serverPort));
        System.out.println(hello);

        // b) Nhận thông điệp "requestId;a1,a2,...,a50"
        byte[] buf = new byte[65535];
        DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
        socket.receive(recvPacket);
        String resp = new String(recvPacket.getData(), 0, recvPacket.getLength(),
                StandardCharsets.UTF_8).trim();
        System.out.println("[RECV ] " + resp);

        String[] parts = resp.split(";", 2);
        String requestId = parts[0];
        int[] nums = Arrays.stream(parts[1].split(","))
                .mapToInt(Integer::parseInt).toArray();

        // c) Tìm max/min và gửi "requestId;max,min"
        int max = Arrays.stream(nums).max().getAsInt();
        int min = Arrays.stream(nums).min().getAsInt();
        String answer = requestId + ";" + max + "," + min;

        byte[] ansBytes = answer.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(ansBytes, ansBytes.length,
                InetAddress.getByName(serverHost), serverPort));
        System.out.println("[SENT ] " + answer);

        // d) Đóng socket
        socket.close();
    }
}
