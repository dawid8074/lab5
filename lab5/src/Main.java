
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Main {

    public static void main( String[] args ) throws IOException {
        //wczytywanie obrazków
        Image img = ImageIO.read( new File( "obraz.png" ) );
        Image img2=ImageIO.read( new File( "invert-obraz.png" ) );
        invertImage("obraz.png");
        Image img3 = ImageIO.read( new File( "obraz.png" ) );
        Image img4 = ImageIO.read( new File( "obraz.png" ) );
        Image img5 = ImageIO.read( new File( "obraz2.jpg" ) );
        // Obrazki gamma- transformacje potęgowe
        gammaTranverse(toBufferedImage(img3),0.3);
        gammaTranverse(toBufferedImage(img4),2);


        new JFrame(){
            {
                JLabel label= new JLabel("Przyciemnione :        Normalne:           Rozjasnione:          Negatyw:               Gamma 0.3:               Gamma 2.0:");
                add(label);
                label.setBounds(10,50,1000,100);
                JLabel label2= new JLabel("Dodawanie:          Odejmowanie:          Różnica:              Mnożenie:              Negacja:                Mnożenie odwrotności:          Ciemniejsze:      Przezroczystość ");
                add(label2);
                label2.setBounds(10,270,1000,100);

                JLabel label3= new JLabel("Jaśniejsze:          Wyłączenie:          Nakładka:              Ostre Światło:              Łagodne światło:                Rozcieńczenie:          Wypalanie:      Reflect");
                add(label3);
                label3.setBounds(10,530,1000,100);

                setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

                setSize( 1000, 1000 );
                setLocationRelativeTo( null );
                add( new JPanel(){
                    @Override
                    protected void paintComponent( Graphics g ) {
                        // paint, który odpowiada za inicjacje wszystkich obrazków
                        super.paintComponent( g );
                        int imgWidth = img.getWidth( null );
                        int imgHeight = img.getHeight( null );
                        int columns = 3;
                        int count = 1;

                        for (int j = 0; j < columns; j++) {
                            // w pętli zostawiłem obrazki, których zmienia się tylko jasność
                            g.drawImage(newBrightness(img, 1f / (columns) * count), imgWidth * j, imgHeight, null);
                            count++;
                        }
                        g.drawImage(img2,imgWidth*3,imgHeight,null);
                        g.drawImage(img3,imgWidth*4,imgHeight,null);
                        g.drawImage(img4,imgWidth*5,imgHeight,null);
                        try {
                            g.drawImage(BlendImage(img,img5,"Dodawanie"),imgWidth*0,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Odejmowanie"),imgWidth*1,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Różnica"),imgWidth*2,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Mnożenie"),imgWidth*3,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Negacja"),imgWidth*4,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Mnożenie odwrotności"),imgWidth*5,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Ciemniejsze"),imgWidth*6,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Jasniejsze"),imgWidth*0,imgHeight*5,null);
                            g.drawImage(BlendImage(img,img5,"wylaczenie"),imgWidth*1,imgHeight*5,null);
                            g.drawImage(BlendImage(img,img5,"Nakładka"),imgWidth*2,imgHeight*5,null);g.drawImage(BlendImage(img,img5,"Różnica"),imgWidth*2,imgHeight*3,null);
                            g.drawImage(BlendImage(img,img5,"Ostre światło"),imgWidth*3,imgHeight*5,null);
                            g.drawImage(BlendImage(img,img5,"Łagodne światło"),imgWidth*4,imgHeight*5,null);
                            g.drawImage(BlendImage(img,img5,"Rozcieńczenie"),imgWidth*5,imgHeight*5,null);

                            g.drawImage(BlendImage(img,img5,"wypalanie"),imgWidth*6,imgHeight*5,null);
                            g.drawImage(BlendImage(img,img5,"Reflect"),imgWidth*7,imgHeight*5,null);

                            g.drawImage(BlendImage(img,img5,"Przezroczystość"),imgWidth*7,imgHeight*3,null);




                        } catch (IOException e) {
                            System.out.println("draw Image nie dziala");;
                        }




                    }
                });


            }

        }.setVisible( true );



    }



    private static void gammaTranverse(BufferedImage image, double gamma) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {

                // Konwertuje RGB na różne wartości
                int rgb = image.getRGB(i, j);
                double R = (rgb >> 16) & 0xff;
                double G = (rgb >> 8) & 0xff;
                double B = rgb & 0xff;

                // Dokunouje zamiany gamma
                R = 255 * Math.pow(R / 255, gamma);
                G = 255 * Math.pow(G / 255, gamma);
                B = 255 * Math.pow(B / 255, gamma);
                // Konwertuje wartości na rgb
                rgb = ((clamp(255,0,255) & 0xff) << 24) | ((clamp((int) R,(int)G,(int)B) & 0xff) << 16) | ((clamp((int) G,0,255) & 0xff) << 8)
                        | ((clamp((int) B,0,255) & 0xff));
                image.setRGB(i, j, rgb);
            }
        }
    }
    public static Image newBrightness( Image source, float brightnessPercentage ) {
        //wywołuje klase bufferedimage
        BufferedImage bi = new BufferedImage(
                source.getWidth( null ),
                source.getHeight( null ),
                BufferedImage.TYPE_INT_ARGB );

        int[] pixel = { 0, 0, 0, 0 };
        float[] hsbvals = { 0, 0, 0 };
        //drukuje obrazek
        bi.getGraphics().drawImage( source, 0, 0, null );

        // przekonwertowuje kazdy piksel i zmieniam jego jasnosc
        for ( int i = 0; i < bi.getHeight(); i++ ) {
            for ( int j = 0; j < bi.getWidth(); j++ ) {

                // pobieram dane z pixela
                bi.getRaster().getPixel( j, i, pixel );

                // konwertuje dane hsb by zmienić jasność
                Color.RGBtoHSB( pixel[0], pixel[1], pixel[2], hsbvals );

                //tworze nowy kolor ze zmienioną jasnością
                Color c = new Color( Color.HSBtoRGB( hsbvals[0], hsbvals[1], hsbvals[2] * brightnessPercentage ) );

                // ustawiam nowy piksel
                bi.getRaster().setPixel( j, i, new int[]{ c.getRed(), c.getGreen(), c.getBlue(), pixel[3] } );

            }

        }

        return bi;

    }

    public static void invertImage(String imageName) {
        //Funkcja odpowiedziala za zapisanie negatywu zdjęcia

        BufferedImage inputFile = null;
        try {
            inputFile = ImageIO.read(new File(imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < inputFile.getWidth(); x++) {
            for (int y = 0; y < inputFile.getHeight(); y++) {
                int rgba = inputFile.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                        255 - col.getGreen(),
                        255 - col.getBlue());
                inputFile.setRGB(x, y, col.getRGB());
            }
        }

        try {
            File outputFile = new File("invert-"+imageName);
            ImageIO.write(inputFile, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    public static BufferedImage toBufferedImage(Image img)
    {
        //Funkcja oddpowiedziala za zamiane image na bufferedimage
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // tworzenie bufferedimage z przezroczystoscią
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // rysuje obrazek na bufferedimage
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // zwracam bufferedimage
        return bimage;
    }
    public static Image BlendImage(Image source,Image source2,String Algorithm) throws IOException {
        //Funkcja działająca identycznie jak funkcja odpowiadająca za rozjaśnianie, dodany jest tylko switch odpowiedzialny za konkretne wartości dla danego przypadku
        BufferedImage bi = new BufferedImage(
                source.getWidth(null),
                source.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        BufferedImage bi2 = new BufferedImage(
                source2.getWidth(null),
                source2.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        int[] pixel = {0, 0, 0, 0};
        float[] hsbvals = {0, 0, 0};

        int[] pixel2 = {0, 0, 0, 0};
        float[] hsbvals2 = {0, 0, 0};


        bi.getGraphics().drawImage(source, 0, 0, null);
        bi2.getGraphics().drawImage(source2, 0, 0, null);

        int temp=0;
//odczyt pixeli obrazu w dwóch pętlach po kolumnach i wierszach
        for (int i = 0; i < bi.getHeight(); i++) {
            for (int j = 0; j < bi.getWidth(); j++) {
//odczyt składowych koloru RGB

                bi.getRaster().getPixel(j, i, pixel);
                bi2.getRaster().getPixel(j, i, pixel2);

                Color.RGBtoHSB(pixel[0], pixel[1], pixel[2], hsbvals);
                Color.RGBtoHSB(pixel2[0], pixel2[1], pixel2[2], hsbvals2);
                Color c1 = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
                Color c2 = new Color(Color.HSBtoRGB(hsbvals2[0], hsbvals2[1], hsbvals2[2]));

                float x,y,z;
                switch (Algorithm){
                    case"Dodawanie":{
                        x=hsbvals[0]+hsbvals2[0];
                        y=hsbvals[1]+hsbvals2[1];
                        z=hsbvals[2]+hsbvals2[2];

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        //Wywołujemy klase color i przypisujemy mu wartości rgb i ustawiamy je następnie na buffered image
//                        i na samym końcu zwracamy bufferedimage po wyjsciu z pętli i to się powtarza dla każdego kolejnego case'a
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Odejmowanie":{
                        x=hsbvals[0]+hsbvals2[0]-1;
                        y=hsbvals[1]+hsbvals2[1]-1;
                        z=hsbvals[2]+hsbvals2[2]-1;
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;

                    }
                    case"Różnica": {
                        x=Math.abs(hsbvals[0]+hsbvals2[0]);
                        y=Math.abs(hsbvals[1]+hsbvals2[1]);
                        z=Math.abs(hsbvals[2]+hsbvals2[2]);

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Mnożenie":{
                        x=hsbvals[0]*hsbvals2[0];
                        y=hsbvals[1]*hsbvals2[1];
                        z=hsbvals[2]*hsbvals2[2];

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Negacja":{
                        x=1-Math.abs(1-hsbvals[0]-hsbvals2[0]);
                        y=1-Math.abs(1-hsbvals[1]-hsbvals2[1]);
                        z=1-Math.abs(1-hsbvals[2]-hsbvals2[2]);
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Mnożenie odwrotności":{
                        x=1-(1-hsbvals[0])*(1-hsbvals2[0]);
                        y=1-(1-hsbvals[1])*(1-hsbvals2[1]);
                        z=1-(1-hsbvals[2])*(1-hsbvals2[2]);

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Ciemniejsze":{
                        if(hsbvals[0]<hsbvals2[0]){
                            x=hsbvals[0];
                        }
                        else{
                            x=hsbvals2[0];
                        }
                        if(hsbvals[1]<hsbvals2[1]){
                            y=hsbvals[0];
                        }
                        else{
                            y=hsbvals2[0];
                        }
                        if(hsbvals[1]<hsbvals2[2]){
                            z=hsbvals[0];
                        }
                        else{
                            z=hsbvals2[0];
                        }
                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Jasniejsze":{
                        if(hsbvals[0]>hsbvals2[0]){
                            x=hsbvals[0];
                        }
                        else{
                            x=hsbvals2[0];
                        }
                        if(hsbvals[1]>hsbvals2[1]){
                            y=hsbvals[0];
                        }
                        else{
                            y=hsbvals2[0];
                        }
                        if(hsbvals[1]>hsbvals2[2]){
                            z=hsbvals[0];
                        }
                        else{
                            z=hsbvals2[0];
                        }
                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"wylaczenie":{
                        x=hsbvals[0]+hsbvals2[0]-2*hsbvals[0]*hsbvals2[0];
                        y=hsbvals[1]+hsbvals2[1]-2*hsbvals[1]*hsbvals2[1];
                        z=hsbvals[2]+hsbvals2[2]-2*hsbvals[2]*hsbvals2[2];

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Nakładka":{
                        if(hsbvals[0]<1/2){
                            x=2*hsbvals[0]*hsbvals2[0];
                        }
                        else{
                            x=1-2*(1-hsbvals[0])*(1-hsbvals2[0]);
                        }
                        if(hsbvals[1]<1/2){
                            y=2*hsbvals[1]*hsbvals2[1];
                        }
                        else{
                            y=1-2*(1-hsbvals[1])*(1-hsbvals2[1]);
                        }
                        if(hsbvals[2]<1/2){
                            z=2*hsbvals[2]*hsbvals2[2];
                        }
                        else{
                            z=1-2*(1-hsbvals[2])*(1-hsbvals2[2]);
                        }

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;

                    }
                    case"Ostre światło":{
                        if(hsbvals2[0]<1/2){
                            x=2*hsbvals[0]*hsbvals2[0];
                        }
                        else{
                            x=1-2*(1-hsbvals[0])*(1-hsbvals2[0]);
                        }
                        if(hsbvals2[1]<1/2){
                            y=2*hsbvals[1]*hsbvals2[1];
                        }
                        else{
                            y=1-2*(1-hsbvals[1])*(1-hsbvals2[1]);
                        }
                        if(hsbvals2[2]<1/2){
                            z=2*hsbvals[2]*hsbvals2[2];
                        }
                        else{
                            z=1-2*(1-hsbvals[2])*(1-hsbvals2[2]);
                        }

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Łagodne światło":{
                        if(hsbvals[0]<1/2){
                            x=2*hsbvals[0]*hsbvals2[0]+hsbvals[0]*hsbvals[0]*(1-2*hsbvals2[0]);
                        }
                        else{
                            x=(float) Math.sqrt(hsbvals[0])*(2*hsbvals2[0]-1)+(2*hsbvals[0]*(1-hsbvals2[0]));
                        }
                        if(hsbvals[1]<1/2){
                            y=2*hsbvals[1]*hsbvals2[1]+hsbvals[1]*hsbvals[1]*(1-2*hsbvals2[1]);

                        }
                        else{
                            y=(float) Math.sqrt(hsbvals[1])*(2*hsbvals2[1]-1)+(2*hsbvals[1]*(1-hsbvals2[1]));
                        }
                        if(hsbvals[2]<1/2){
                            z=2*hsbvals[2]*hsbvals2[2]+hsbvals[2]*hsbvals[2]*(1-2*hsbvals2[2]);

                        }
                        else{
                            z=(float) Math.sqrt(hsbvals[2])*(2*hsbvals2[2]-1)+(2*hsbvals[2]*(1-hsbvals2[2]));
                        }
                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Rozcieńczenie":{
                        x=hsbvals[0]/(1-hsbvals2[0]);
                        y=hsbvals[0]/(1-hsbvals2[0]);
                        z=hsbvals[2]+hsbvals2[2]-2*hsbvals[2]*hsbvals2[2];

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"wypalanie":{
                        x=1-(1-hsbvals[0])/(hsbvals2[0]);
                        y=1-(1-hsbvals[1])/(hsbvals2[1]);
                        z=1-(1-hsbvals[2])/(hsbvals2[2]);
                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Reflect":{
                        x=(hsbvals[0]*hsbvals[0])/(1-hsbvals2[0]);


                        y=(hsbvals[1]*hsbvals[1])/(1-hsbvals2[1]);
                        z=(hsbvals[2]*hsbvals[2])/(1-hsbvals2[2]);

                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }
                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() ,
                                c.getGreen() ,
                                c.getBlue(), pixel[3]
                        });
                        break;
                    }
                    case"Przezroczystość":{
                        float alfa=(float) 0.5;


                        x= (1-alfa)*(hsbvals2[0]+alfa)*hsbvals[0];
                        y=(1-alfa)*(hsbvals2[1]+alfa)*hsbvals[1];
                        z=(1-alfa)*(hsbvals2[2]+alfa)*hsbvals[2];
                        if(x>255){
                            x=255;
                        }
                        if(x<0){
                            x=0;
                        }
                        if(y>255){
                            y=255;
                        }
                        if(y<0){
                            y=0;
                        }if(z>255){
                            z=255;
                        }
                        if(z<0){
                            z=0;
                        }

                        Color c = new Color(Color.HSBtoRGB(x,y,z));
                        bi.getRaster().setPixel(j, i, new int[]{
                                c.getRed() , c.getGreen() ,
                                c.getBlue(), pixel[3]}
                        );
                        break;
                    }

                }

            }
        }

        return bi;
    }

}
