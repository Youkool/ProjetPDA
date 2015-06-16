package datas;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class Photo implements Serializable {
	private static final long serialVersionUID = 0;
	private String imageURL;
	private String titre;
	private String auteur;
	private String pays;
	private GregorianCalendar date;
	private String[] keyWords;
	private final int[] tag;
	private BufferedImage img;

	/**
	 * Constructeur sans parametre de la classe photo
	 * Les attributs sont modifies par la suite par les getteurs/setteurs
	 */
	public Photo(String urlOrigine, String urlDestination){
		this.imageURL = urlDestination;
		this.titre = this.imageURL.split("/")[this.imageURL.split("/").length-1];
		this.titre = this.titre.substring(0,this.titre.length()-(this.getExtension().length()+1));
		this.auteur = null;
		this.pays = null;
		this.date = new GregorianCalendar();
		this.keyWords = new String[0];
		try {
			this.CopierFichier(urlOrigine);
			this.tagInvisible();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.tag = this.generateTag();
	}

	/**
	 * 
	 * @param source
	 */
	private void CopierFichier(String source) throws IOException{
		this.img = ImageIO.read(new File(source));
		this.enregisterImg();
		this.img = ImageIO.read(new File(this.imageURL));
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void enregisterImg() throws IOException{
		String extension = this.getExtension();
		File f = new File(this.imageURL);
		f.createNewFile();
		if(extension.equals("jpg") || extension.equals("jpeg")){
			ImageIO.write(this.img, "png", f);
		} else {
			ImageIO.write(this.img, extension, f);
		}
	}

	/**
	 * 
	 */
	public void tagInvisible() throws IOException{
		int alpha = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		int width = this.img.getWidth();
		int height = this.img.getHeight();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if(col != width-1 || row != height-1){
					Color rgb = new Color(this.img.getRGB(col, row));
					alpha += rgb.getAlpha();
					r += rgb.getRed();
					g += rgb.getGreen();
					b += rgb.getBlue();
				}
			}
		}
		alpha %= 255;
		r %= 255;
		g %= 255;
		b %= 255;
		Color tag = new Color(alpha,r,g,b);
		this.img.setRGB(width-1, height-1, tag.getRGB());
		this.enregisterImg();
	}

	/**
	 * Genere le tag de securite de la photo
	 */
	private int[] generateTag(){
		int width = this.img.getWidth();
		int height = this.img.getHeight();
		int p1 = this.img.getRGB(0, 0);
		int p2 = this.img.getRGB(width-1, height-1);
		return new int[]{width,height,p1,p2};
	}


	/**
	 * Verifie que le tag de securite correspond bien a la photo de l'url 
	 * @return True si la photo correspond au tag, false sinon.
	 * @throws IOException 
	 */
	public boolean checkTag(){
		boolean ret = true;
		BufferedImage imgTest;
		try {
			imgTest = ImageIO.read(new File(this.imageURL));
			int width = imgTest.getWidth();
			int height = imgTest.getHeight();
			int p1 = imgTest.getRGB(0, 0);
			int p2 = imgTest.getRGB(width-1, height-1);
			int[] tagTest = new int[]{width,height,p1,p2};
			for(int i = 0; i<4;i++){
				if(this.tag[i] != tagTest[i]){
					ret = false;
				}
			}
		} catch (IOException e) {
			ret = false;
		}
		return ret;
	}

	/**
	 * Genere la courbe de couleur sous la forme d'un tableau de 765 entiers entre 0 et 100.
	 * Un entier pour chacune des 255 nuances de rouge, vert et bleu. 
	 * Si l'entier vaut 0, cela veut dire que aucun pixel de l'image ne possede cette nuance de couleur.
	 * Si l'entier vaut 100, cela veut dire que tous les pixels de l'image possedent cette nuance de couleur.
	 * Un pixel etant code un code RGB (Red Green Blue), chacun possede trois nuances : une rouge, une verte et unee bleue, codee chacune sur 255 
	 * @return Un tableau de 765 entiers allant de 0 a 100.
	 */
	public int[] courbeCouleur(){
		int[] ret = new int[768];
		int width = this.img.getWidth();
		int height = this.img.getHeight();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Color rgb = new Color(this.img.getRGB(col, row));
				int r = rgb.getRed();
				int g = rgb.getGreen() + 256;
				int b = rgb.getBlue() + 512;
				ret[r]++;
				ret[g]++;
				ret[b]++;
			}
		}
		int max = 0;
		for (int e : ret){
			if(e>max){
				max = e;
			}
		}
		for (int e : ret){
			double r = ((double)e/max) * 100;
			e = (int)r;
		}
		return ret;
	}

	/**
	 * Compare les courbes de couleur des deux photographies.
	 * @param p2 la photo a comparer
	 * @return Le quotient de difference entre 0 et 100. 
	 * 0 signifie que les courbes de couleurs sont identiques, 100 signifie que les courbes de couleurs sont totalement differentes.
	 */
	public int differenceCouleur(Photo p2){
		int[] tab1 = this.courbeCouleur();
		int[] tab2 = p2.courbeCouleur();
		int ret = 0;
		for(int i = 0; i<tab1.length; i++){
			ret += Math.abs(tab1[i]-tab2[i]);			
		}
		ret = (int)(((double)ret/768)*100);
		return ret;
	}

	/**
	 * Compare cette photographie et verifie si elle est identiques (au pixel pres) a celle passee en parametre.
	 * @param p2 La photographie a comparer
	 * @return True si les deux photogrpahies sont strictement identiques (au pixel pres), false sinon.
	 */
	public boolean isIdentique(Photo p2){
		boolean ret = false;
		if(this.img.getHeight()==p2.img.getHeight() && this.img.getWidth()==p2.img.getWidth()){
			ret = true;
			int row = 0;
			int col = 0;
			while(ret && row<this.img.getHeight()){
				col = 0;
				while(ret && col<this.img.getWidth()){
					if(this.img.getRGB(col,row)!=p2.img.getRGB(col,row)) ret = false;
					col++;
				}
				row++;
			}
		}
		return ret;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return this.imageURL;
	}
	/**
	 * @return the keyWords
	 */
	public String[] getKeyWords() {
		return this.keyWords;
	}
	/**
	 * @return the titre
	 */
	public String getTitre() {
		return this.titre;
	}
	/**
	 * @return the auteur
	 */
	public String getAuteur() {
		return this.auteur;
	}
	/**
	 * @return the pays
	 */
	public String getPays() {
		return this.pays;
	}
	/**
	 * @return the date
	 */
	public GregorianCalendar getDate() {
		return this.date;
	}

	/**
	 * @return the img
	 */
	public BufferedImage getImg(){
		return this.img;
	}

	/**
	 * @return the type of the image
	 */
	public String getExtension(){
		String ext = "";
		int i = this.imageURL.length()-1;
		char c = this.imageURL.charAt(i);
		do {
			ext = c + ext;
			i--;
			c = this.imageURL.charAt(i);
		} while(c != '.');
		return ext;
	}

	/**
	 * @param titre the titre to set
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}
	/**
	 * @param auteur the auteur to set
	 */
	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}
	/**
	 * @param pays the pays to set
	 */
	public void setPays(String pays) {
		this.pays = pays;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	/**
	 * @param keyWords the keyWords to set
	 */
	public void setKeyWords(String[] keyWords) {
		this.keyWords = keyWords;
	}
}
