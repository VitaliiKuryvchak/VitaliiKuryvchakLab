import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            String message = "Anyone there?";
            byte[] data = message.getBytes();

            DatagramPacket packet = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName("255.255.255.255"), 8888
            );

            socket.send(packet);
            System.out.println("Broadcast message sent");


            socket.setSoTimeout(3000);
            byte[] buffer = new byte[256];
            Set<String> responses = new HashSet<>();

            while (true) {
                try {
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);

                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    String from = responsePacket.getAddress().getHostAddress();

                    if (responses.add(from)) {
                        System.out.println("Response from " + from + ": " + response);
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Waiting ended.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
