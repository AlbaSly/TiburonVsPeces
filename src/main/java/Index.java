import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

class Tablero extends JPanel {

    private JFrame frame;
    public static Graphics g;
    public static final int WIDTH = 1200;
    public static final int HEIGTH = 630;

    public static boolean isJuegoTerminado = false;
    private Image imagenFondo;

    public static LinkedList<Animal> enemigos = new LinkedList<>();
    public static Animal tiburon;

    public Tablero() {
        this.configurarContenedorPadre();
        this.configurarResolucion();
        this.instanciarEntidades();
    }

    @Override
    public void paint(Graphics graphics) {
        g = graphics;
        this.configurarFondo();
    }

    private void configurarContenedorPadre() {
        this.frame = new JFrame();

        this.frame.setTitle("Peces vs Tiburones | Raxel Arias");
        this.frame.setSize(WIDTH, HEIGTH);

        this.frame.getContentPane().add(this);

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.setResizable(false);
    }
    private void configurarFondo() {
        this.imagenFondo = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();

        g.drawImage(this.imagenFondo, 0, 0, 1200, 630, this);

        this.setOpaque(false);
        super.paint(g);
    }

    private void configurarResolucion() {
        this.setLayout(null);
        this.setSize(WIDTH, HEIGTH);
    }

    private void instanciarEntidades() {

        this.generarProtagonista();
        this.generarEnemigos(4);
    }

    private void generarProtagonista() {
        tiburon = new Tiburon(
                new JLabel(),
                this,
                "shark.png",
                144,
                64,
                this.frame
        );
        tiburon.setLocation(0, HEIGTH/2);
    }

    private void generarEnemigos(int nEnemigos) {

        int contador = 1;

        for (int x = 1; x <=  nEnemigos; x++) {

            String nombreImagen = null;

            if (contador == 1) nombreImagen = "clownfish.png";
            if (contador == 2) nombreImagen = "tortoise.png";
            if (contador == 3) {
                contador = 0;
                nombreImagen = "whale.png";
            }

            contador++;

            Animal pez = new Pez(
                    new JLabel(),
                    this,
                    nombreImagen,
                    100,
                    100,
                    this.frame
            );

            pez.setLocation(WIDTH-240, 80*x);
        }
    }
}

public class Index {

    public static void main(String[] args) {
        Tablero tablero = new Tablero();
    }
}
