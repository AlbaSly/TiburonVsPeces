import javax.swing.*;

public abstract class Animal {

    private final String nombreImagen;
    private final int SPRITE_SIZE_WIDTH;
    private final int SPRITE_SIZE_HEIGTH;

    private final Tablero tablero;
    private final JLabel etiquetaAnimal;

    public Animal(JLabel etiquetaAnimal, Tablero tablero, String nombreImagen, int imagenWidth, int imagenHeigth) {

        this.tablero = tablero;
        this.etiquetaAnimal = etiquetaAnimal;
        this.nombreImagen = nombreImagen;

        this.SPRITE_SIZE_WIDTH = imagenWidth;
        this.SPRITE_SIZE_HEIGTH = imagenHeigth;
    }

    abstract void atacar();

    abstract void actualizarAnimacion();

    abstract void morir();

    public void setLocation(int x, int y) {
        this.etiquetaAnimal.setBounds(x, y, this.SPRITE_SIZE_WIDTH, this.SPRITE_SIZE_HEIGTH);
        this.tablero.add(this.etiquetaAnimal);
    }
    public Tablero getTablero() {return this.tablero;}
    public String getNombreImagen() {
        return this.nombreImagen;
    }

    public JLabel getEtiquetaAnimal() {
        return this.etiquetaAnimal;
    }

    public int getAnimalPosX() {
        return this.etiquetaAnimal.getLocation().x;
    }

    public int getAnimalPosY() {
        return this.etiquetaAnimal.getLocation().y;
    }

    public void setAnimalPosicion(int x, int y) {
        this.getEtiquetaAnimal().setLocation(x, y);
    }
}
