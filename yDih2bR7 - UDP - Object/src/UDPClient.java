// [Mã câu hỏi (qCode): yDih2bR7].  Thông tin sản phẩm vì một lý do nào đó đã bị sửa đổi thành không đúng, cụ thể:
// a.	Tên sản phẩm bị đổi ngược từ đầu tiên và từ cuối cùng, ví dụ: “lenovo thinkpad T520” bị chuyển thành “T520 thinkpad lenovo”
// b.	Số lượng sản phẩm cũng bị đảo ngược giá trị, ví dụ từ 9981 thành 1899

// Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. 
// Yêu cầu là xây dựng một chương trình client giao tiếp với server để gửi/nhận các sản phẩm theo mô tả dưới đây:
// a.	Đối tượng trao đổi là thể hiện của lớp Product được mô tả như sau
// •	Tên đầy đủ của lớp: UDP.Product
// •	Các thuộc tính: id String, code String, name String, quantity int
// •	Một hàm khởi tạo có đầy đủ các thuộc tính được liệt kê ở trên
// •	Trường dữ liệu: private static final long serialVersionUID = 20161107; 
// b.	Giao tiếp với server theo kịch bản
// •       Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng “;studentCode;qCode”. Ví dụ: “;B15DCCN001;EE29C059”

// •	Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Product từ server. 
// Trong đối tượng này, các thuộc tính id, name và quantity đã được thiết lập giá trị.
// •	Sửa các thông tin sai của đối tượng về tên và số lượng như mô tả ở trên và gửi đối tượng vừa được sửa đổi lên server theo cấu trúc:
// 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Product đã được sửa đổi.
// •	Đóng socket và kết thúc chương trình.

import UDP.Product;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private static final String SERVER_HOST = "203.162.10.109";
    private static final int SERVER_PORT = 2209;

    public static void main(String[] args) {
        String studentCode = "B22DCVT090";
        String qCode = "yDih2bR7";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(15000); // 15s đề phòng treo nhận

            InetAddress serverAddr = InetAddress.getByName(SERVER_HOST);

            // 1) Gửi chuỗi theo định dạng “;studentCode;qCode”
            String firstMessage = ";" + studentCode + ";" + qCode;
            byte[] firstBytes = firstMessage.getBytes(StandardCharsets.UTF_8);
            DatagramPacket firstPacket = new DatagramPacket(firstBytes, firstBytes.length, serverAddr, SERVER_PORT);
            socket.send(firstPacket);
            System.out.println("[Client] Sent: " + firstMessage);

            // 2) Nhận gói: 8 byte đầu là requestId, phần còn lại là đối tượng Product (đã set id, name, quantity)
            byte[] buf = new byte[64 * 1024]; // đủ lớn cho payload UDP
            DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
            socket.receive(recvPacket);

            // Sao chép chính xác dữ liệu nhận được (tránh phần thừa của buffer)
            byte[] data = new byte[recvPacket.getLength()];
            System.arraycopy(buf, 0, data, 0, recvPacket.getLength());

            if (data.length < 8) {
                throw new IOException("Invalid packet: less than 8 bytes.");
            }

            // Lấy 8 byte requestId (giữ nguyên 8 byte thô để gửi trả)
            byte[] requestIdBytes = new byte[8];
            System.arraycopy(data, 0, requestIdBytes, 0, 8);
            String requestId = new String(requestIdBytes, StandardCharsets.UTF_8);
            System.out.println("[Client] Received requestId: '" + requestId + "'");

            // Phần còn lại là object Product đã serialize
            int objLen = data.length - 8;
            if (objLen <= 0) {
                throw new IOException("Invalid packet: no object payload.");
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(data, 8, objLen);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();

            if (!(obj instanceof Product)) {
                throw new IOException("Invalid object type: expected UDP.Product");
            }

            Product p = (Product) obj;
            System.out.println("[Client] Received Product (raw): " + p);

            // 3) Sửa name và quantity
            p.name = fixNameByReversingWords(p.name);
            p.quantity = fixQuantityByReversingDigits(p.quantity);

            System.out.println("[Client] Fixed Product: " + p);

            // 4) Gửi lại: 8 byte requestId + serialized Product
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // viết 8 byte requestId y như đã nhận
            baos.write(requestIdBytes);

            // serialize object
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(p);
            oos.flush();
            byte[] sendBytes = baos.toByteArray();

            DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, serverAddr, SERVER_PORT);
            socket.send(sendPacket);
            System.out.println("[Client] Sent fixed Product back to server. Done.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ví dụ sai: "T520 thinkpad lenovo" -> đúng: "lenovo thinkpad T520"
    private static String fixNameByReversingWords(String name) {
        if (name == null) return null;
        String trimmed = name.trim().replaceAll("\\s+", " ");
        String[] parts = trimmed.split(" ");
        if (parts.length < 2) return trimmed; // 0 hoặc 1 từ thì giữ nguyên
        // Hoán đổi phần tử đầu và cuối, giữ nguyên phần giữa
        String tmp = parts[0];
        parts[0] = parts[parts.length - 1];
        parts[parts.length - 1] = tmp;
        return String.join(" ", parts);
    }


    // Ví dụ sai: 1899 -> đúng: 9981 (đảo chữ số)
    private static int fixQuantityByReversingDigits(int qty) {
        boolean negative = qty < 0;
        String s = Integer.toString(Math.abs(qty));
        String reversed = new StringBuilder(s).reverse().toString();
        try {
            int val = Integer.parseInt(reversed);
            return negative ? -val : val;
        } catch (NumberFormatException ex) {
            // Trường hợp hiếm gặp (không nên xảy ra), trả về qty gốc
            return qty;
        }
    }
}

