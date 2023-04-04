package productmanage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductClient {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Scanner sc = new Scanner(System.in);

    //메소드: 서버 연결
    public void connect() throws IOException {
        socket = new Socket("localhost", 50001);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("[클라이언트] 서버에 연결됨");
    }

    //메소드: JSON 받기
    public void receive() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String json = dis.readUTF();
                    //dis(DataInputStream) 을 UTF로 읽어 String 형태로 초기화
                    JSONObject root = new JSONObject(json);
                    //JSON객체 root 생성, 값은 UTF로 읽어온 json 변수
                    String status = root.getString("status");
                    JSONArray data = root.getJSONArray("data");

                    System.out.println("[상품 목록]");
                    System.out.println("----------------------------------------------------");
                    System.out.printf("%3s  %22s  %10s %11s%n","no","name","price","stock");
//                    System.out.println("no      name                   price       stock    ");
                    System.out.println("----------------------------------------------------");

                    for (Object obj : data) {
                        JSONObject product = (JSONObject) obj;

                        int productNo = product.getInt("no");
                        String productName = product.getString("name");
                        int productPrice = product.getInt("price");
                        int productStock = product.getInt("stock");
                        System.out.printf("%2d %24s %11d %10d%n",productNo,productName,productPrice,productStock);
                    }
                        System.out.println("----------------------------------------------------");
                        System.out.println("메뉴: 1.Create | 2.Update | 3.Delete | 4.Exit");
                        System.out.print("선택: ");
                }
            } catch (Exception e1) {
                System.out.println("[클라이언트] 서버 연결 끊김");
                System.exit(0);
            }
        });
        thread.start();
    }

    //메소드: JSON 보내기
    public void send(String json) throws IOException {
        dos.writeUTF(json);
        //dos(DataOutputStream)에 입력한 채팅을 writeUTF로 작성
        dos.flush();
        //dos 버퍼 비우기
    }

    //메소드: 서버 연결 종료
    public void unconnect() throws IOException {
        socket.close();
        //연결 종료
    }

    //메소드: 메인
    public static void main(String[] args) {
        try {
            ProductClient productClient = new ProductClient();
            Scanner scanner = new Scanner(System.in);

            productClient.connect();
            productClient.receive();


            boolean check = true;
            while (check) {
                String menu = scanner.next();

                JSONObject dataJsonObject = new JSONObject();

                switch (menu){
                    case "1":
                        productClient.create(dataJsonObject);
                        break;
                    case "2":
                        productClient.update(dataJsonObject);
                        break;
                    case "3":
                        productClient.delete(dataJsonObject);
                        break;
                    case "4":
                        check = false;
                        break;
                    default:
                        System.out.println("1~4까지 수만 넣을 수 있습니다.");
                        System.out.print("선택: ");
                        continue;
                }

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("menu", menu);
                jsonObject.put("data", dataJsonObject);

                String json = jsonObject.toString();
                productClient.send(json);
            }
            scanner.close();
            //scanner close
            productClient.unconnect();
            //chatClient uncoonet 연결 종료1
        } catch (IOException e) {
            System.out.println("[클라이언트] 서버 연결 안됨");
        }
    }

    public int create(JSONObject jsonObject) {
        try {
            System.out.println();
            System.out.println("[상품 생성]");
            System.out.print("상품 이름: ");
            jsonObject.put("name", sc.next());
            System.out.print("상품 가격: ");
            jsonObject.put("price", sc.nextInt());
            System.out.print("상품 재고: ");
            jsonObject.put("stock", sc.nextInt());
        }catch(Exception e) {
            System.out.println("잘못된 값을 입력하였습니다. 다시 시도해주세요.");
            return 0;
        } return 1;
    }
    public int update(JSONObject jsonObject) {

        try {
            System.out.println("[상품 수정]");
            System.out.print("상품 번호: ");
            jsonObject.put("no", sc.nextInt());
            System.out.print("상품 이름: ");
            jsonObject.put("name", sc.next());
            System.out.print("상품 가격: ");
            jsonObject.put("price", sc.nextInt());
            System.out.print("상품 재고: ");
            jsonObject.put("stock", sc.nextInt());
        }catch(Exception e) {
            System.out.println("잘못된 값을 입력하였습니다. 다시 시도해주세요.");
            return 0;
        }
        return 1;
    }
    public void delete(JSONObject jsonObject) {

        System.out.println("[상품 삭제]");
        System.out.print("상품 번호: ");
        jsonObject.put("no",sc.nextInt());
        return;
    }
}