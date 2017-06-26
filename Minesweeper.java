import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper implements ActionListener, MouseListener {
    JFrame f;
    JButton newgame;
    JButton cheat;
    JLabel width;
    JLabel height;
    JLabel seed;
    JLabel discovered;
    JLabel flagged;
    JLabel mines;
    MinePanel mp;

    public Minesweeper() {
        f = new JFrame("Minesweeper");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setSize(1235, 1039);
        f.setLayout(null);

        mp = new MinePanel(this);
        mp.setSize(1001, 1001);
        mp.setLocation(0, 0);
        mp.addMouseListener(this);
        f.add(mp);

        newgame = new JButton("Neues Spiel");
        newgame.addActionListener(this);
        newgame.setSize(200, 25);
        newgame.setLocation(1010, 10);
        f.add(newgame);

        cheat = new JButton("Cheat");
        cheat.addActionListener(this);
        cheat.setSize(200, 25);
        cheat.setLocation(1010, 40);
        f.add(cheat);

        width = new JLabel("Width: 20");
        width.setSize(120, 25);
        width.setLocation(1010, 80);
        f.add(width);

        height = new JLabel("Height: 20");
        height.setSize(120, 25);
        height.setLocation(1010, 110);
        f.add(height);

        seed = new JLabel("Seed: " + mp.playground.seed);
        seed.setSize(200, 25);
        seed.setLocation(1010, 140);
        f.add(seed);

        mines = new JLabel("Mines: 40");
        mines.setLocation(1010, 170);
        mines.setSize(120, 25);
        f.add(mines);

        discovered = new JLabel("Discovered cells: 0");
        discovered.setLocation(1010, 200);
        discovered.setSize(170, 25);
        f.add(discovered);

        flagged = new JLabel("Flagged cells: 0");
        flagged.setLocation(1010, 230);
        flagged.setSize(120, 25);
        f.add(flagged);

        f.setVisible(true);
    }

    public void mouseExited(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseClicked(MouseEvent e) {
        updateLabels();
    }

    public void actionPerformed(ActionEvent a) {
        //Funktionen der Schaltflächen
        if(a.getSource() == newgame) {
            mp.newgame();
        } else if (a.getSource() == cheat) {
            mp.cheat();
        }
        updateLabels();
    }

    public void updateLabels() {
        width.setText("Width: " + Integer.toString(mp.playground.cellsX));
        f.add(width);
        height.setText("Height; " + Integer.toString(mp.playground.cellsY));
        f.add(height);
        seed.setText("Seed: " + mp.playground.seed);
        f.add(seed);
        mines.setText("Mines: " + mp.playground.mineCount);
        f.add(mines);
        discovered.setText("Discovered cells: " + mp.playground.cellsDiscovered);
        f.add(discovered);
        flagged.setText("Flagged cells: " + mp.playground.flaggedCount);
        f.add(flagged);
        f.repaint();
    }
}
