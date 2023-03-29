package xmpp.client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class XMPPClient {
    private XMPPTCPConnectionConfiguration config;
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    public void start(String username, String password, String domain, String host) throws IOException, SmackException, XMPPException, InterruptedException {
        // Disconnect existing connection in case client want to switch credentials
        if(connection != null) {
            connection.disconnect();
        }

        config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain(domain)
                .setHost(host)
                .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login();
        System.out.println("Connection success...");

        chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                System.out.println("New message from " + from + ": " + message.getBody());
            }
        });
    }
    public void sendMsgToUser(String targetUser, String msg) throws SmackException.NotConnectedException, InterruptedException, XmppStringprepException {
        EntityBareJid jid = JidCreate.entityBareFrom(targetUser);
        Chat chat = chatManager.chatWith(jid);
        chat.send(msg);
        System.out.printf("Message sent to: %s\n" +
                "Content: %s\n", targetUser, msg);
    }

    public void disconnect() {
        connection.disconnect();
    }

    public static void main(String[] args) {
        try {
            XMPPClient sender = new XMPPClient();
            sender.start("httung120303", "123456789", "jabber.hot-chilli.net", "jabber.hot-chilli.net");

            XMPPClient receiver = new XMPPClient();
            receiver.start("httung120303", "123456789", "hot-chilli.eu", "jabber.hot-chilli.net");

            sender.sendMsgToUser("httung120303@hot-chilli.eu", "Hello");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while(true) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
