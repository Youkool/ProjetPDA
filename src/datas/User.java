package datas;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Classe implementant les methodes pour gerer un utilisateur.
 * @author FRETAY Juliette et LE POLLES--POTIN Leandre - Groupe 1C
 */
public class User implements Serializable {
	private static final long serialVersionUID = 0;
	/**
	 * Le nom de l'utilisateur.
	 */
	private String nom;

	/**
	 * Le mot de passe de l'utilisateur.
	 */
	private String password;

	/**
	 * Le chemin du repertoire dans lequel toutes les photographies de cet utilisateur sont stockees.
	 */
	private String urlDossier;

	/**
	 * {@link Collection} contenant toutes les {@link Photo} appartenant a cet utilisateur.
	 */
	private Collection allPhotos;

	/**
	 * {@link Hashtable} contenant toutes les collections crees par cet utilisateur.
	 */
	private Hashtable<String,Collection> collections;

	/**
	 * Cree un {@link User} dont le {@link #nom} est "Default" et {@link #urlDossier} est "saves/Default" (en chemin relatif).
	 */
	public User(){
		this.nom = "default";
		this.urlDossier = "saves/"+this.nom;
		File dossier = new File(this.urlDossier);
		dossier.mkdirs();
		this.allPhotos = new Collection("All");
		this.collections = new Hashtable<String,Collection>();
	}

	/**
	 * Cree un {@link User} dont le {@link #nom} est passe en parametre et {@link #urlDossier} est "saves/{@link #nom}" (en chemin relatif).
	 * @param nom Le nom de l'utilisateur.
	 */
	public User(String nom){
		this.nom = nom;
		this.urlDossier = "saves/"+nom;
		File dossier = new File(this.urlDossier);
		dossier.mkdirs();
		this.allPhotos = new Collection("All");
		this.collections = new Hashtable<String,Collection>();
	}

	/**
	 * Cree un {@link User} dont le {@link #nom} et {@link #urlDossier} sont passes en parametre.
	 * @param nom Le nom de l'utilisateur.
	 * @param url Le chemin du dossier de stockage des photographies de cet utilisateur.
	 */
	public User(String nom, String url){
		this.nom = nom;
		this.urlDossier = url+"/"+nom;
		File dossier = new File(this.urlDossier);
		dossier.mkdirs();
		this.allPhotos = new Collection("All");
		this.collections = new Hashtable<String,Collection>();
	}

	// --- Methodes ---

	/**
	 * Cree une nouvelle {@link Collection} dont le nom est passe en parametre.
	 * Cette {@link Collection} est ensuite ajoutee a {@link #collections} avec pour cle le nom passe en parametre.
	 * Si le nom existe deja, la {@link Collection} n'est pas cree.
	 * @param key Le nom de la {@link Collection} a creer.
	 */
	public void addCollection(String key){
		if(key != null && !key.equalsIgnoreCase("All") && !this.collections.containsKey(key)){
			Collection c = new Collection(key);
			this.collections.put(key,c);
		}
	}

	/**
	 * Supprime la {@link Collection} dont le nom est passe en parametre.
	 * Les {@link Photo} contenues dans cette {@link Collection} sont conservees du moment qu'elles ne sont pas explicitement supprimees par {@link #delPhoto(String)}.
	 * @param key Le nom de la {@link Collection} a supprimer.
	 */
	public void delCollection(String key){
		if(key != null && !key.equalsIgnoreCase("All")){
			Enumeration<Photo> enumPhoto = this.collections.get(key).toutesPhotos();
			while(enumPhoto.hasMoreElements()){
				Photo p = enumPhoto.nextElement();
				p.setCollection("All");
			}
			this.collections.remove(key);
		}
	}

	/**
	 * Deplace une {@link Photo} specifie d'une {@link Collection} a une autre.
	 * Si la categorie d'origine est "All", la {@link Photo} n'est pas supprimee de "All" mais juste ajoutee a la {@link Collection} de destination.
	 * A l'inverse si la categorie de destination est "All", la {@link Photo} est simplement supprimee de la {@link Collection} d'origine.
	 * Si la collection d'origine et la collection de destination sont identiques, cette methode ne fait rien.
	 * @param keyPhoto 
	 * @param keyOrigine
	 * @param keyDestination
	 */
	public void movePhoto(String keyPhoto, String keyOrigine,  String keyDestination){
		String key = this.urlDossier+"/"+keyPhoto;
		if(keyOrigine != null && key != null && keyDestination != null && !keyOrigine.equals(keyDestination)){
			if (this.collections.containsKey(keyOrigine) && this.collections.containsKey(keyDestination)){
				try {
					this.collections.get(keyDestination).addPhoto(this.collections.get(keyOrigine).getPhoto(key));
					this.collections.get(keyOrigine).getPhoto(key).setCollection(this.collections.get(keyDestination).getTitre());
					this.collections.get(keyOrigine).delPhoto(key);
				} catch (NoPhotoFoundException e) {
					e.printStackTrace();
				}
			} else if (keyDestination.equalsIgnoreCase("All") && this.collections.containsKey(keyOrigine)){
				try {
					this.collections.get(keyOrigine).delPhoto(key);
					this.allPhotos.getPhoto(key).setCollection("All");
				} catch (NoPhotoFoundException e) {
					e.printStackTrace();
				}
			} else if (keyOrigine.equalsIgnoreCase("All") && this.collections.containsKey(keyDestination)){
				try {
					this.collections.get(keyDestination).addPhoto(this.allPhotos.getPhoto(key));
					this.allPhotos.getPhoto(key).setCollection(this.collections.get(keyDestination).getTitre());
				} catch (NoPhotoFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Supprime la {@link Photo} dont le {@link Photo#getNomFichier()} est passe en parametre.
	 * @param keyPhoto La cle de la photo.
	 */
	public void delPhoto(String key){
		try {
			this.allPhotos.delPhoto(key);
		} catch (NoPhotoFoundException e) {
			e.printStackTrace();
		}
		File f = new File(this.urlDossier+"/"+key);
		f.delete();
		for(String e : this.collections.keySet()){
			try {
				this.collections.get(e).delPhoto(key);
			} catch (NoPhotoFoundException e1) {}
		}
	}

	/**
	 * Importe la photo situe a l'url passe en parametre
	 * @param url L'url de la photo a importer
	 * @return Le nom de la photo
	 */
	public String importerPhoto(String url){
		String ret = "";
		String nom = url.split("/")[url.split("/").length-1];
		String newUrl = this.urlDossier+"/"+nom;
		Photo p;
		try {
			p = new Photo(url,newUrl);
			this.allPhotos.addPhoto(p);
			ret = p.getNomFichier();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Accesseur de l'attribut {@link #nom}.
	 * @return {@link #nom}
	 */
	public String getNom() {
		return this.nom;
	}

	/**
	 * Accesseur de l'attribut {@link #urlDossier}.
	 * @return {@link #urlDossier}
	 */
	public String getUrlDossier() {
		return this.urlDossier;
	}

	/**
	 * Accesseur de l'attribut {@link #allPhotos}.
	 * @return {@link #allPhotos}
	 */
	public Collection getAllPhotos() {
		return this.allPhotos;
	}

	/**
	 * Accesseur renvoyant la {@link Collection} dans {@link #collections} dont le nom est passe en parametre.
	 * @param key Le nom de la {@link Collection} recherchee.
	 * @return La {@link Collection} dont le nom correspond au parametre. Renvoie <code>null</code> sinon.
	 */
	public Collection getCollection(String key){
		return this.collections.get(key);
	}

	/**
	 * Accesseur de l'attribut {@link #password}.
	 * @return {@link #password}
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Accesseur renvoyant une {@link Enumeration} de {@link Collection} contenues par {@link #collections}.
	 * @return Une {@link Enumeration} de {@link Collection} contenues par {@link #collections}.
	 */
	public Enumeration<Collection> toutesCollections() {
		return this.collections.elements();
	}

	/**
	 * @param password La nouvelle valeur de {@link #password}.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param nom La nouvelle valeur de {@link #nom}.
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @param urlDossier La nouvelle valeur de {@link #urlDossier}.
	 */
	public void setUrlDossier(String urlDossier) {
		this.urlDossier = urlDossier;
	}

	// --- Sauver et Charger ---
	/**
	 * Supprime tout les fichiers lies a cet utilisateur.
	 * Cela comprends le dossier a l'emplacement {@link #urlDossier} ainsi que toutes les images qu'il contient.
	 * Le fichier.out correspondant a cet utilisateur est supprime egalement.
	 */
	public void delUser(){
		File delUser = new File(this.urlDossier+".out");
		delUser.delete();
		Enumeration<Photo> toutePhotos = this.allPhotos.toutesPhotos();
		while(toutePhotos.hasMoreElements()){
			Photo p = toutePhotos.nextElement();
			String key = p.getNomFichier();
			this.delPhoto(key);
		}
		delUser = new File(this.urlDossier);
		delUser.delete();
	}

	/**
	 * Sauvegarde cet objet {@link User} a l'emplacement par defaut.
	 * L'emplacement par defaut est un fichier d'extension ".out" situe au meme niveau que le dossier {@link #urlDossier}.
	 */
	public void sauver(){
		this.sauver(this.urlDossier+".out");
	}

	/**
	 * Sauvegarde cet objet {@link User} a l'emplacement passe en parametre.
	 * @param url Chemin de sauvegarde du fichier objet.
	 */
	public void sauver(String url){
		FileOutputStream file;
		ObjectOutputStream flux;
		try {
			file = new FileOutputStream(url); 
			flux = new ObjectOutputStream(file);
			flux.writeObject(this);
			flux.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Charge et retourne un {@link User}.
	 * @param url L'emplacement d'ou charger le {@link User}
	 * @return Un objet de type {@link User}.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static User charger(String url) throws IOException, ClassNotFoundException{
		FileInputStream file;
		ObjectInputStream flux;
		User ret = null;
		file = new FileInputStream(url);
		flux = new ObjectInputStream(file);
		ret = (User) flux.readObject();
		flux.close();
		return ret;
	}
}
