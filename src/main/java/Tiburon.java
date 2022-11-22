import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class Tiburon extends Animal implements Runnable {

    private final JFrame contenedorTablero;
    private static Tiburon current;
    private static Thread tiburonThread;
    private static int nVidas = 3;
    private static LinkedList<JLabel> etiquetasVidas = new LinkedList<>();
    private static int nBalas = 0;

    public Tiburon(JLabel etiquetaAnimal, Tablero tablero, String nombreImagen, int imagenWidth, int imagenHeigth, JFrame contenedorTablero) {
        super(etiquetaAnimal, tablero, nombreImagen, imagenWidth, imagenHeigth);

        this.contenedorTablero = contenedorTablero;

        current = this;

        this.configurarMovimientos();
        this.cargarVidas();
        this.generarHilo();

        this.actualizarCorazones();
    }

    private void generarHilo() {
        tiburonThread = new Thread(this);
        tiburonThread.start();
    }

    private void cargarVidas() {
        for (int x = 0; x < Tiburon.nVidas; x++) {
            etiquetasVidas.push(new JLabel());
        }
    }

    @Override
    public void run() {
        while (true) {
            if (Tablero.isJuegoTerminado) break;

            this.actualizarAnimacion();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}

        }
    }

    @Override
    public void atacar() {

        if (nBalas > 4) return;

        ImageIcon dienteImg = new ImageIcon(this.getClass().getResource("/imagenes/shark_teeth.png"));
        JLabel etiquetaDiente = new JLabel();

        etiquetaDiente.setBounds(current.getAnimalPosX()+80, current.getAnimalPosY()+20, 32, 32);

        this.getTablero().add(etiquetaDiente);

        nBalas++;

        Thread threadDiente = new Thread(() -> {

            while (!this.isEnemigoAlcanzado(etiquetaDiente)) {
                if (Tablero.isJuegoTerminado) break;

                if (etiquetaDiente.getLocation().x >= Tablero.WIDTH-200) {
                    this.getTablero().remove(etiquetaDiente);
                    this.contenedorTablero.getContentPane().repaint();
                    nBalas--;
                    break;
                }

                etiquetaDiente.setLocation(etiquetaDiente.getLocation().x+20, etiquetaDiente.getLocation().y);
                etiquetaDiente.setIcon(dienteImg);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        threadDiente.start();
    }

    private boolean isEnemigoAlcanzado(JLabel etiquetaDiente) {
        System.out.println(Tablero.enemigos.toString());
        for (int x = 0; x < Tablero.enemigos.size(); x++) {

            int enemigoX = Tablero.enemigos.get(x).getAnimalPosX();
            int enemigoY = Tablero.enemigos.get(x).getAnimalPosY();
            int enemigoWidth = Tablero.enemigos.get(x).getEtiquetaAnimal().getBounds().width;
            int enemigoHeigth = Tablero.enemigos.get(x).getEtiquetaAnimal().getBounds().height;

            int dienteX = etiquetaDiente.getLocation().x;
            int dienteY = etiquetaDiente.getLocation().y;

            //Los 32 pixeles son el ancho y largo de la figura de la bala del diente

            if (dienteX+32 >= enemigoX && dienteX+32 <= enemigoX+enemigoWidth) {
                if (dienteY+32 >= enemigoY && dienteY+32 <= enemigoY+enemigoHeigth) {
                    System.out.println("Enemigo atinado a " + Tablero.enemigos.get(x));
                    Tablero.enemigos.get(x).morir();

                    this.getTablero().remove(etiquetaDiente);
                    this.contenedorTablero.getContentPane().repaint();
                    nBalas--;

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void actualizarAnimacion() {
        if (Tablero.isJuegoTerminado) return;

        ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/" + this.getNombreImagen()));

        this.getEtiquetaAnimal().setIcon(image);
        this.getEtiquetaAnimal().setBorder(new LineBorder(Color.BLACK));


    }

    private void actualizarCorazones() {
        ImageIcon heartImg = new ImageIcon(this.getClass().getResource("/imagenes/heart.png"));

        //Limpiar
        for (int x = 0; x < nVidas; x++) {
            if (etiquetasVidas.get(x) != null) {
                this.getTablero().remove(etiquetasVidas.get(x));
            }
        }

        //Rellenar
        for (int x = 0; x < etiquetasVidas.size(); x++) {

            etiquetasVidas.get(x).setBounds((60*x)+8, 0, 60, 60);
            etiquetasVidas.get(x).setIcon(heartImg);

            this.getTablero().add(etiquetasVidas.get(x));
        }

        this.contenedorTablero.getContentPane().repaint();
    }

    private void configurarMovimientos() {
        this.contenedorTablero.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                Tiburon.moverse(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    private static void moverse(KeyEvent ev) {
        if (Tablero.isJuegoTerminado) return;

        int keyCode = ev.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            Tiburon.current.setAnimalPosicion(
                    Tiburon.current.getAnimalPosX(),
                    Tiburon.current.getAnimalPosY()-20
            );
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            Tiburon.current.setAnimalPosicion(
                    Tiburon.current.getAnimalPosX(),
                    Tiburon.current.getAnimalPosY()+20
            );
        }

        if (keyCode == KeyEvent.VK_SPACE) {
            Tiburon.current.atacar();
        }
    }

    public void bajarVida() {

        nVidas--;
        etiquetasVidas.pop();
        this.actualizarCorazones();

        if (nVidas == 0) {
            this.morir();
        }
    }
    @Override
    public void morir() {

        this.getTablero().remove(this.getEtiquetaAnimal());
        this.contenedorTablero.getContentPane().repaint();

        //Desaparecer del interno estático
        current = null;
        //Desaparecer del externo estático
        Tablero.tiburon = null;

        Tablero.isJuegoTerminado = true;
        JOptionPane.showMessageDialog(null, "Han ganado los peces");
    }
}
