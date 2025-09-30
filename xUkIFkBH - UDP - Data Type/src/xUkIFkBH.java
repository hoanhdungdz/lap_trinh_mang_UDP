// [Mã câu hỏi (qCode): xUkIFkBH].  
// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
// a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng “;studentCode;qCode”. Ví dụ: “;B15DCCN004;99D9F604”
// b. Nhận thông điệp là một chuỗi từ server theo định dạng “requestId;z1,z2,...,z50” requestId là chuỗi ngẫu nhiên duy nhất
//     z1 -> z50 là 50 số nguyên ngẫu nhiên
//     c. Thực hiện tính số lớn thứ hai và số nhỏ thứ hai của thông điệp trong z1 -> z50 và gửi thông điệp lên server theo định dạng “requestId;secondMax,secondMin”
//     d. Đóng socket và kết thúc chương trình

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.TreeSet;

public class xUkIFkBH {
    public static void main(String[] args) throws Exception {
        String serverHost = "203.162.10.109";
        int serverPort = 2207;
        String studentCode = "B22DCVT090";
        String qCode = "xUkIFkBH";

        // a) Gửi thông điệp ";studentCode;qCode"
        String hello = ";" + studentCode + ";" + qCode;
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(7000);
        byte[] send = hello.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(send, send.length,
                InetAddress.getByName(serverHost), serverPort));
        System.out.println("[SENT] " + hello);

        // b) Nhận "requestId;z1,z2,...,z50"
        byte[] buf = new byte[65535];
        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        socket.receive(pkt);
        String resp = new String(pkt.getData(), 0, pkt.getLength(), StandardCharsets.UTF_8).trim();
        System.out.println("[RECV] " + resp);

        String[] parts = resp.split(";", 2);
        String requestId = parts[0];
        int[] zs = Arrays.stream(parts[1].split(",")).mapToInt(Integer::parseInt).toArray();

        // c) Tính số lớn thứ hai và nhỏ thứ hai theo GIÁ TRỊ PHÂN BIỆT
        //    Nếu không đủ 2 giá trị khác nhau, dùng chính max/min.
        TreeSet<Integer> set = new TreeSet<>();
        for (int v : zs) set.add(v);
        int min = set.first(), max = set.last();
        Integer secondMinV = set.higher(min);   // phần tử lớn hơn min nhỏ nhất
        Integer secondMaxV = set.lower(max);    // phần tử nhỏ hơn max lớn nhất
        int secondMin = (secondMinV != null) ? secondMinV : min;
        int secondMax = (secondMaxV != null) ? secondMaxV : max;

        String answer = requestId + ";" + secondMax + "," + secondMin;
        byte[] ans = answer.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(ans, ans.length,
                InetAddress.getByName(serverHost), serverPort));
        System.out.println("[SENT] " + answer);

        // d) Đóng socket
        socket.close();
    }
}

