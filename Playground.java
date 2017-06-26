import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.util.*;

class Playground {
    MinePanel parent;
    int cellWidth;
    int cellHeight;
    int cellsX;
    int cellsY;
    int mineCount;
    long seed;
    int flaggedCount;
    int cellsDiscovered;
    //Discovered cells with neighbors which havn't been "dealt with"
    private ArrayList<Point> dcwn;

    private int neighbors[][];
    private boolean hasMine[][];
    private boolean flagged[][];
    private boolean discovered[][];

    public Playground(MinePanel nparent, int ncw, int nch, int ncx, int ncy, int nmineCount, long nseed) {
        parent = nparent;
        seed = nseed;
        cellWidth = ncw;
        cellHeight = nch;
        cellsX = ncx;
        cellsY = ncy;
        mineCount = nmineCount;
        hasMine = new boolean[ncx][ncy];
        flagged = new boolean[ncx][ncy];
        neighbors = new int[ncx][ncy];
        discovered = new boolean[ncx][ncy];
        dcwn = new ArrayList<Point>();

        int minesSet = 0;
        Random rand = new Random(seed);
        while(minesSet < mineCount) {
            int x = rand.nextInt(ncx);
            int y = rand.nextInt(ncy);
            if(!hasMine[x][y]) {
                hasMine[x][y] = true;
                minesSet++;
            }
        }

        for(int x = 0; x < ncx; x++) {
            for(int y = 0; y < ncy; y++) {
                neighbors[x][y] = countNeighbors(x, y, "hasMine");
            }
        }
    }

    public int handleMousePress(MouseEvent e) {
        int x = (int) (e.getX() / cellWidth);
        int y = (int) (e.getY() / cellHeight);
        if(e.getButton() == MouseEvent.BUTTON1) {
            if(!flagged[x][y]) {
                if(hasMine[x][y]) {
                    //Game over
                    JOptionPane.showMessageDialog(null, "Du hast auf eine Mine geklickt! Game over.");
                    return 1;
                } else {
                    recDiscover(x, y, dcwn.listIterator());
                    if(cellsX * cellsY - cellsDiscovered == mineCount) {
                        JOptionPane.showMessageDialog(null, "Alle Minen entdeckt!");
                        return 1;
                    }
                }
            }
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            //Rechte Maustaste
            changeFlagged(x, y);
        }
        return 0;
    }

    private void recDiscover(int x, int y, ListIterator<Point> iter) {
        if(x >= 0 && x < cellsX && y >= 0 && y < cellsY && !hasMine[x][y] && !discovered[x][y] && !flagged[x][y]) {
            discovered[x][y] = true;
            ++cellsDiscovered;
            if(neighbors[x][y] == 0) {
                for(int xi = x - 1; xi < x + 2; xi++) {
                    for(int yi = y - 1; yi < y + 2; yi++) {
                        if(x != xi || y != yi) {
                            recDiscover(xi, yi, iter);
                        }
                    }
                }
                recDiscover(x - 1, y, iter);
                recDiscover(x + 1, y, iter);
                recDiscover(x, y - 1, iter);
                recDiscover(x, y + 1, iter);
            } else {
                iter.add(new Point(x, y));
            }
        }
    }

    public void paint(Graphics g) {
        for(int x = 0; x < cellsX; x++) {
            for(int y = 0; y < cellsY; y++) {
                if(discovered[x][y]) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                    g.setColor(Color.BLACK);
                    if(neighbors[x][y] > 0) {
                        g.drawString(Integer.toString(neighbors[x][y]), (int) ((x + 0.25) * cellWidth), (int) ((y + 0.75) * cellHeight));
                    }
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                    if(flagged[x][y]) {
                        g.setColor(Color.BLACK);
                        g.drawString("F", (int) ((x + 0.25) * cellWidth), (int) ((y + 0.75) * cellHeight));
                    }
                }
                g.setColor(Color.BLACK);
                g.drawRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    public int cheat() {
        boolean cheated = false;
        for(ListIterator<Point> iter = dcwn.listIterator(); iter.hasNext() && !cheated;) {
            Point p = iter.next();
            if(countNeighbors(p.x, p.y, "flagged") + countNeighbors(p.x, p.y, "discovered") == countNeighbors(p.x, p.y, "cells")) {
                iter.remove();
            } else if(countNeighbors(p.x, p.y, "flagged") == neighbors[p.x][p.y]) {
                cheated = true;
                iter.remove();
                for(int xi = p.x - 1; xi < p.x + 2; xi++) {
                    for(int yi = p.y - 1; yi < p.y + 2; yi++) {
                        if((xi != p.x || yi != p.y)) {
                            recDiscover(xi, yi, iter);
                        }
                    }
                }
            } else {
                int toflagneighs = countNeighbors(p.x, p.y, "cells") - countNeighbors(p.x, p.y, "discovered");
                if (toflagneighs == neighbors[p.x][p.y]) {
                    cheated = true;
                    iter.remove();
                    for(int xi = p.x - 1; xi < p.x + 2 && toflagneighs > 0; xi++) {
                        for(int yi = p.y - 1; yi < p.y + 2 && toflagneighs > 0; yi++) {
                            if((xi != p.x || yi != p.y) && xi >= 0 && xi < cellsX && yi >= 0 && yi < cellsY && !discovered[xi][yi]) {
                                if(!flagged[xi][yi]) flaggedCount++;
                                flagged[xi][yi] = true;
                                --toflagneighs;
                            }
                        }
                    }
                }
            }
        }
        if(cellsX * cellsY - cellsDiscovered == mineCount) {
            return 2;
        }
        return cheated ? 1 : 0;
    }

    private int countNeighbors(int x, int y, String typeToCount) {
        int hits = 0;
        if(typeToCount == "cells") {
            if((x == 0 || x == cellsX - 1) && (y == 0 || y == cellsY - 1)) {
                hits = 3;
            } else if(x == 0 || x == cellsX - 1 || y == 0 || y == cellsY - 1) {
                hits = 5;
            } else {
                hits = 8;
            }
        } else {
            for(int xi = x - 1; xi < x + 2; xi++) {
                for(int yi = y - 1; yi < y + 2; yi++) {
                    if((xi != x || yi != y) && xi > -1 && yi > -1 && xi < cellsX && yi < cellsY) {
                        switch(typeToCount) {
                            case "flagged":
                            if(flagged[xi][yi]) hits++;
                            break;
                            case "discovered":
                            if(discovered[xi][yi]) hits++;
                            break;
                            case "hasMine":
                            if(hasMine[xi][yi]) hits++;
                            break;
                        }
                    }
                }
            }
        }
        return hits;
    }
    
    
    public void changeFlagged(int x, int y) {
        flagged[x][y] = !flagged[x][y];
        flaggedCount += flagged[x][y] ? 1 : -1;
    }
}
