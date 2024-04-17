import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton GOButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // membuat JFrame pertama (jendela aplikasi)
            JFrame frame = new JFrame("Flappy Bird - Main Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // mengatur aksi ketika menutup jendela
            frame.setSize(360, 640); // menetapkan ukuran jendela

            // membuat panel dan menambahkan GridBagLayout
            JPanel panel = new JPanel(new GridBagLayout());

            // membuat label dan menambahkannya ke panel
            JLabel label = new JLabel("Ready?");

            // menambahkan label ke panel dengan konfigurasi GridBagLayout agar berada di tengah
            GridBagConstraints gbcLabel = new GridBagConstraints();
            gbcLabel.gridx = 0;
            gbcLabel.gridy = 0;
            gbcLabel.insets = new Insets(10, 0, 10, 0); // memberikan jarak antara komponen
            panel.add(label, gbcLabel);

            // membuat tombol dan menambahkan action listener
            JButton button = new JButton("GO!");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // menutup frame MainMenu
                    frame.dispose();

                    // membuat JFrame kedua (jendela permainan)
                    JFrame gameFrame = new JFrame("Flappy Bird - Game");
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.setSize(frame.getSize()); // mengatur ukuran sama dengan JFrame pertama
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setResizable(false);

                    // buat objek JPanel permainan
                    FlappyBird flappyBird = new FlappyBird();
                    gameFrame.add(flappyBird);
                    gameFrame.setVisible(true);

                    // panggil requestFocus setelah panel ditambahkan ke frame
                    flappyBird.requestFocus();
                }
            });

            // menambahkan tombol ke panel dengan konfigurasi GridBagLayout agar berada di bawah label
            GridBagConstraints gbcButton = new GridBagConstraints();
            gbcButton.gridx = 0;
            gbcButton.gridy = 1;
            gbcButton.insets = new Insets(10, 0, 10, 0); // memberikan jarak antar komponen
            panel.add(button, gbcButton);

            // menambahkan panel ke frame
            frame.add(panel);

            // menampilkan frame pertama di tengah layar
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
