import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // lebar dan tinggi jendela permainan
    int frameWidth = 360;
    int frameHeight = 640;

    // gambar
    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    // posisi awal pemain di sumbu x dan y
    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    // lebar dan tinggi pemain
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;  // objek pemain

    // posisi awal pipa di sumbu x dan y
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    // lebar dan tinggi pipa
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;  // ArrayList untuk menyimpan pipa

    Timer gameLoop;  // timer untuk game loop
    int gravity = 1;  // besarnya gravitasi
    Timer pipesCooldown;  // timer untuk cooldown antara penempatan pipa
    // status permainan dimulai dan berakhir
    private boolean gameStarted = false;
    private boolean gameOver = false;
    JLabel scoreLabel;  // JLabel untuk menampilkan skor
    int score = 0;  // skor pemain
    private boolean passedPipePair = false;  // status pemain ketika melewati sepasang pipa

    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));  // set ukuran panel
        setBackground(Color.blue);  // set warna latar belakang
        setFocusable(true);  // mengatur panel dapat fokus
        addKeyListener(this);  // menambahkan KeyListener ke panel

        // menginisialisasi gambar-gambar
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        // buat JLabel untuk menampilkan skor
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setBounds(10, 10, 150, 30);
        add(scoreLabel);

        // inisialisasi pemain dan ArrayList pipa
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        // timer untuk cooldown antara penempatan pipa
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pipa");
                placePipes();  // tempatkan pipa baru
            }
        });
        pipesCooldown.start();  // mulai timer

        gameLoop = new Timer(1000/60, this);  // timer untuk game loop
        gameLoop.start(); // mulai timer
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);  // gambar pemain

        // gambar pipa-pipa
        for(int i = 0 ; i < pipes.size() ; i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }

        // gambar skor
        scoreLabel.setText("Score: " + score);
    }

    // metode untuk menggerakkan elemen-elemen permainan
    public void move() {
        if (gameStarted) {
            // menggerakkan pemain
            player.setVelocityY(player.getVelocityY() + gravity);
            player.setPosY(player.getPosY() + player.getVelocityY());
            player.setPosY(Math.max(player.getPosY(), 0));

            Iterator<Pipe> iterator = pipes.iterator();
            while (iterator.hasNext()) {
                Pipe pipe = iterator.next();
                pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());
                // hapus pipa yang sudah keluar dari layar
                if (pipe.getPosX() + pipe.getWidth() < 0) {
                    iterator.remove();
                }
            }

            // periksa apakah pemain melewati sepasang pipa
            for (int i = 0; i < pipes.size(); i += 2) {
                Pipe upperPipe = pipes.get(i);
                Pipe lowerPipe = pipes.get(i + 1);

                // periksa tabrakan antara pemain dan pipa atas
                if (isCollision(player, upperPipe)) {
                    gameOver();  // jika terjadi tabrakan, permainan berakhir
                    return;
                }

                // periksa tabrakan antara pemain dan pipa bawah
                if (isCollision(player, lowerPipe)) {
                    gameOver();  // jika terjadi tabrakan, permainan berakhir
                    return;
                }

                // periksa jika pemain melewati sepasang pipa (pipa atas dan bawah)
                if (upperPipe.getPosX() + upperPipe.getWidth() < player.getPosX() && !upperPipe.isPassed() && !passedPipePair) {
                    upperPipe.setPassed(true);
                    lowerPipe.setPassed(true);
                    passedPipePair = true; // set passedPipePair menjadi true
                    score++; // tambah skor saat pemain melewati sepasang pipa
                    scoreLabel.setText("Score: " + score);
                }
            }

            // periksa jika pemain jatuh ke batas bawah layar
            if (player.getPosY() + player.getHeight() >= frameHeight) {
                gameOver();  // jika jatuh ke batas bawah layar, permainan berakhir
                return;
            }
        }
    }

    // metode untuk mendeteksi tabrakan antara pemain dan pipa
    private boolean isCollision(Player player, Pipe pipe) {
        Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());
        Rectangle pipeRect = new Rectangle(pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight());
        return playerRect.intersects(pipeRect);
    }

    // metode untuk menangani permainan berakhir
    public void gameOver() {
        gameLoop.stop();  // berhenti dari game loop
        pipesCooldown.stop();  // berhenti dari cooldown penempatan pipa
        gameOver = true;  // set status gameOver menjadi true

        // membuat panel untuk menampung teks
        JPanel messagePanel = new JPanel(new GridLayout(2, 1)); // Mengatur layout menjadi grid 2 baris, 1 kolom
        messagePanel.setPreferredSize(new Dimension(200, 100)); // Mengatur ukuran panel

        // menambahkan label teks "Game Over!" ke panel
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Mengatur batas atas

        // menambahkan label teks "Press 'R' to restart" ke panel
        JLabel restartLabel = new JLabel("Press 'R' to restart", SwingConstants.CENTER);
        restartLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Mengatur batas bawah

        // menambahkan JLabel ke panel
        messagePanel.add(gameOverLabel);
        messagePanel.add(restartLabel);

        // menampilkan pesan dialog dengan panel sebagai komponennya
        JOptionPane.showMessageDialog(this, messagePanel, "Message", JOptionPane.PLAIN_MESSAGE);
    }

    // metode untuk memulai kembali permainan
    public void restartGame() {
        // inisialisasi ulang variabel dan objek permainan
        gameOver = false;
        gameStarted = false;
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        pipesCooldown.start();
        gameLoop.start();

        // reset skor ke 0
        score = 0;
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!gameStarted) {
                    gameStarted = true;
                    player.setVelocityY(-10);  // atur kecepatan awal saat pemain pertama kali melompat
                } else {
                    player.setVelocityY(-10);  // atur kecepatan saat pemain melompat
                }
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restartGame();  // mulai ulang permainan saat tombol 'R' ditekan
            }
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    // metode untuk menempatkan pipa baru
    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;

        // Cek apakah ada pipa di dalam area yang sama dengan pipa yang akan ditempatkan
        for (Pipe pipe : pipes) {
            if (pipe.getPosX() + pipe.getWidth() > pipeStartPosX && pipe.getPosX() < pipeStartPosX + pipeWidth) {
                // Jika ada, atur ulang posisi pipa
                randomPosY = (int) (pipeStartPosY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
                break;
            }
        }

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);

        // Set passedPipePair menjadi false saat menambahkan pipa baru
        passedPipePair = false;

        // Panggil repaint setelah menambahkan pipa
        repaint();
    }
}