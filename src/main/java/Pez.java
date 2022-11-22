import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Pez extends Animal implements Runnable {

    private final JFrame contenedorTablero;
    private Thread pezThread;
    private boolean isMuerto = false;

    public Pez(JLabel etiquetaAnimal, Tablero tablero, String nombreImagen, int imagenWidth, int imagenHeigth, JFrame contenedorTablero) {
        super(etiquetaAnimal, tablero, nombreImagen, imagenWidth, imagenHeigth);

        //Agregar a la lista
        Tablero.enemigos.add(this);
        this.contenedorTablero = contenedorTablero;

        //Iniciar Thread
        this.pezThread = new Thread(this);
        this.pezThread.start();
    }

    @Override
    public void run() {

        Boolean cambio = false;
        int contadorTiempo = 0;

        while (!isMuerto) {
            if (Tablero.isJuegoTerminado) break;
            if (this.getAnimalPosX() <= 0) {
                if (Tablero.enemigos.size() > 0) {
                    Tablero.isJuegoTerminado = true;
                    JOptionPane.showMessageDialog(null, "Han ganado los peces");
                }
                break;
            }

            if (contadorTiempo == 6) cambio = true;
            if (contadorTiempo == -6) cambio = false;

            this.setAnimalPosicion(this.getAnimalPosX()-20, cambio ? this.getAnimalPosY()+20 : this.getAnimalPosY()-20);
            this.actualizarAnimacion();
            if (contadorTiempo % 4 == 0) this.atacar();

            if (cambio) contadorTiempo--;
            if (!cambio) contadorTiempo++;
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {}

        }
    }

    @Override
    public void atacar() {

        ImageIcon bubbleImg = new ImageIcon(this.getClass().getResource("/imagenes/bubble.png"));
        JLabel etiquetaBurbuja = new JLabel();

        etiquetaBurbuja.setBounds(this.getAnimalPosX()-120, this.getAnimalPosY()+60, 24, 24);

        this.getTablero().add(etiquetaBurbuja);

        Thread threadBurbuja = new Thread(() -> {
           while (!this.determinarJugadorAtacado(etiquetaBurbuja)) {
               if (Tablero.isJuegoTerminado) break;

               if (etiquetaBurbuja.getLocation().x <= 0) {
                   this.getTablero().remove(etiquetaBurbuja);
                   this.contenedorTablero.getContentPane().repaint();
                   break;
               }

               etiquetaBurbuja.setLocation(etiquetaBurbuja.getLocation().x-20, etiquetaBurbuja.getLocation().y);
               etiquetaBurbuja.setIcon(bubbleImg);

               try {
                   Thread.sleep(50);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });

        threadBurbuja.start();
    }

    private boolean determinarJugadorAtacado(JLabel etiquetaBurbuja) {
        if (Tablero.isJuegoTerminado) return false;

        int jugadorX = Tablero.tiburon.getAnimalPosX();
        int jugadorY = Tablero.tiburon.getAnimalPosY();
        int jugadorWidth = Tablero.tiburon.getEtiquetaAnimal().getBounds().width;
        int jugadorHeigth = Tablero.tiburon.getEtiquetaAnimal().getBounds().height;

        int bubbleX = etiquetaBurbuja.getLocation().x;
        int bubbleY = etiquetaBurbuja.getLocation().y;

        if (bubbleX > jugadorX && bubbleX <= jugadorX+jugadorWidth) {

            if (bubbleY >= jugadorY && bubbleY <= jugadorY+jugadorHeigth) {
                this.getTablero().remove(etiquetaBurbuja);
                this.contenedorTablero.getContentPane().repaint();

                ((Tiburon) Tablero.tiburon).bajarVida();

                return true;
            }
        }

        return false;
    }

    @Override
    public void morir() {
        isMuerto = true;

        Tablero.enemigos.remove(this);

        this.getTablero().remove(this.getEtiquetaAnimal());
        this.contenedorTablero.getContentPane().repaint();

        if (Tablero.enemigos.size() == 0) {
            Tablero.isJuegoTerminado = true;
            JOptionPane.showMessageDialog(null, "El jugador ha ganado la partida");
        }
    }

    @Override
    public void actualizarAnimacion() {

        ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/" + this.getNombreImagen()));

        this.getEtiquetaAnimal().setIcon(image);
        this.getEtiquetaAnimal().setBorder(new LineBorder(Color.BLACK));
    }
}
