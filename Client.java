import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


public class Client {

    static String clientPath = "C:/Users/han/Desktop/client/";

    public static void main(String[] args) throws Exception{

        Scanner sc = new Scanner(System.in);
        Socket sock;

        DataInputStream dis;
        DataOutputStream dos;

        while(true) {

            sock = new Socket("localhost", 15000);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());

            System.out.println("\n\n메뉴\n1.암호화\n2.복호화\n3.종료\n");
            String menu = sc.nextLine();


            if (menu.equals("1")) {
                // client 파일의 파일 목록을 읽어옴
                File dir = new File(clientPath);
                File[] files = dir.listFiles();

                // 읽어온 파일 목록들을 출력함
                for (int i = 0; i < files.length; i++) {
                    String getName = files[i].getName();
                    System.out.println(i + 1 + " : " + getName);
                }
                // 암호화 파일 선정
                System.out.print("암호화 하실 파일을 입력하세요 : ");
                int fileaNum = sc.nextInt();
                sc.nextLine();

                // 암호화 파일 보내기
                sendFile(sock, files[fileaNum - 1].getName(), 1);

                //암호화 성공
                System.out.println("암호화 완료!");


            } else if (menu.equals("2")) {

                // client 파일의 파일 목록을 읽어옴
                File dir = new File(clientPath);
                File[] files = dir.listFiles();

                // 읽어온 파일 목록들을 출력함
                for (int i = 0; i < files.length; i++) {
                    String getName = files[i].getName();
                    System.out.println(i + 1 + " : " + getName);
                }
                // 복호화 파일 선정
                System.out.print("복호화 하실 파일명을 입력하세요 : ");
                int fileaNum = sc.nextInt();
                sc.nextLine();

                // 복호화 파일 보내기
                sendFile(sock, files[fileaNum - 1].getName(), 2);

                //복호화 성공
                System.out.println("복호화 완료!");
            }
            else {
                System.exit(0);
            }
            continue;
        }
    }
    private static void sendFile(Socket socket, String fileName, int type) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            // 파일 타입 전송 [ 암호화:1 | 복호화: 2 ]
            dos.writeInt(type);

            // 파일 이름 전송
            dos.writeUTF(fileName);

            // 파일 내용 읽기
            byte[] fileContents = Files.readAllBytes(Paths.get(clientPath + fileName));

            // 파일 크기와 내용 전송
            dos.writeInt(fileContents.length);
            dos.write(fileContents);
            dos.writeUTF(clientPath);

            System.out.println("파일 전송 완료: " + fileName);
        }
    }

}
