/* 
 *  PDA project -- UBS/IUT de Vannes -- Dept Informatique
 *
 *  $Id$
 *
 *  Copyright 2007-08 © IUT de Vannes
 *  Ce logiciel pédagogique est diffusé sous licence GPL
 */
package pda.control;

import pda.datas.*;
import pda.view.*;

import javax.swing.*;

import datas.Collection;
import datas.NoPhotoFoundException;
import datas.Selection;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.GregorianCalendar;

/**
 *  The simplest application in the PDA.
 *
 *  It can be used to construct other applications.
 *
 *  @author F. Merciol, D. Deveaux MAJ J-F. Kamp
 *                      <{francois.merciol|daniel.deveaux}@univ-ubs.fr>
 *  @version $Revision: 27 $
 */
public class AppCtrl implements IApplication, ActionListener {

	/*
	 * Private implementation -------------------------------------------------
	 */

	/** the name of the application */
	private String name;

	/** the view of the application */
	private AppView view;

	/** the engine of the application */
	private AppDatas engine;

	/*
	 *  Public ressources -----------------------------------------------------
	 *
	 *  Constructors
	 */

	/**
	 * Initialize the datas and ihm of App application.
	 */
	public AppCtrl () {

		engine = new AppDatas();
		view = new AppView (this, engine );
	} // --------------------------------------------------------- AppCtrl()

	/*
	 *  Public methods
	 */

	/*
	 * See documentation of interface
	 */
	public void start ( PdaCtrl pda ) {
		System.out.println ( "Start of App application" );
	} // -------------------------------------------------------------- start()

	/*
	 * See documentation of interface
	 */
	public String getAppliName() {
		return name;
	} // ---------------------------------------------------------- getAppliName()

	/*
	 * See documentation of interface
	 */
	public JPanel getAppliPanel() {
		return view.getPanel();
	} // ---------------------------------------------------------- getAppliPanel()

	/*
	 * See documentation of interface
	 */
	public boolean close() {
		return true;
	} // ---------------------------------------------------------- close()

	/*
	 * See documentation of interface
	 */
	public void setAppliName ( String theName ) {
		this.name = theName;
	} // ---------------------------------------------------------- setAppliName()

	/**
	 * Callback method, reaction to button pushed
	 * @param e the captured event
	 */
	public void actionPerformed ( ActionEvent e ) {

		// Write here reactions to Events generated by App application



		JButton source =new JButton();
		if(e.getSource().getClass() == source.getClass()){
			source = (JButton)e.getSource();
		}

		if(source.getActionCommand() == "Bouton Galerie"){
			this.view.afficherGalerie(this.engine.getUtilisateurSelect().getAllPhotos());
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Recherche"){
			this.view.afficherRecherche();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Options"){
			this.view.afficherOptions();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Gestion"){
			this.view.afficherGestion();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Edition"){
			this.view.afficherEdition();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Diaporama"){
			try {
				this.view.afficherDiaporama(this.engine.getCollectionSelect().getPhotoSelect());
			} catch (NoPhotoFoundException e1) {
				e1.printStackTrace();
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Accueil"){
			this.view.afficherMenu();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Faire Recherche"){
			System.out.println("Bouton de recherche");
			String[] titre;
			if(this.view.getTexteTitre().getText().trim().equals("")==false && this.view.getTexteTitre() != null){
				titre = this.view.getTexteTitre().getText().split(";");
			}
			else{
				titre = null;
			}
			String categorie;
			if(this.view.getComboCat().getSelectedItem().equals("-")){
				categorie =null;
			}
			else{
				categorie = this.view.getComboCat().getItemAt(this.view.getComboCat().getSelectedIndex());
			}
			String[] auteur;
			if(this.view.getTexteAuteur().getText().trim().equals("") == false && this.view.getTexteAuteur() != null ){
				auteur = this.view.getTexteAuteur().getText().split(";");
			}
			else{
				auteur = null;
			}
			int jour1 = -1;
			if(this.view.getComboJour().getItemAt(this.view.getComboJour().getSelectedIndex()).equals("-") == false){
				jour1 = Integer.parseInt(this.view.getComboJour().getItemAt(this.view.getComboJour().getSelectedIndex()));
			}
			int jour2 = -1;
			if(this.view.getComboJour2().getItemAt(this.view.getComboJour2().getSelectedIndex()).equals("-") == false){
				jour2 = Integer.parseInt(this.view.getComboJour2().getItemAt(this.view.getComboJour2().getSelectedIndex()));
			}
			int mois1 = this.view.getComboMois().getSelectedIndex() - 1;
			int mois2 = this.view.getComboMois2().getSelectedIndex() -1;
			int annee1 = -1;
			if(this.view.getComboAnnee().getItemAt(this.view.getComboAnnee().getSelectedIndex()).equals("-") == false){
				annee1 = Integer.parseInt(this.view.getComboAnnee().getItemAt(this.view.getComboAnnee().getSelectedIndex()));
			}
			int annee2 = -1;
			if(this.view.getComboAnnee2().getItemAt(this.view.getComboAnnee2().getSelectedIndex()).equals("-") == false){
				annee2 = Integer.parseInt(this.view.getComboAnnee2().getItemAt(this.view.getComboAnnee2().getSelectedIndex()));
			}
			String[] localisation;
			if(this.view.getTextePays().getText().trim().equals("")==false && this.view.getTextePays() != null){
				localisation = this.view.getTextePays().getText().split(";");
			}
			else{
				localisation = null;
			}
			
			String[] motsCles;
			if(this.view.getTexteMots().getText().trim().equals("")== false && this.view.getTexteMots() != null){
				motsCles = this.view.getTexteMots().getText().split(";");
			}
			else{
				motsCles = new String[0];
			}
			
			
			Selection laSelection;

			if(categorie != null){
				laSelection = new Selection(this.engine.getUtilisateurSelect().getCollection(categorie));
			}
			else{
				laSelection = new Selection(this.engine.getUtilisateurSelect().getAllPhotos());
			}

			laSelection.setAuteur(auteur);
			laSelection.setKeyWords(motsCles);
			laSelection.setPays(localisation);
			laSelection.setTitre(titre);
			if(annee1 !=-1 && jour1 !=-1 && mois1 !=-1){
				laSelection.setDateDebut(new GregorianCalendar(annee1,mois1,jour1));
				
			}
			else{
				laSelection.setDateDebut(null);
				
			}
			if(annee1 !=-1 && jour1 !=-1 && mois1 !=-1){
				laSelection.setDateFin(new GregorianCalendar(annee2,mois2,jour2));
				
			}
			else{
				laSelection.setDateFin(null);
				
			}
			Collection resultat = laSelection.rechercher();
			if(resultat != null){
				this.engine.setCollectionSelect(resultat);
				this.view.afficherGalerie(resultat);
			} else {
				System.out.println("rien trouve !");
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Back"){
			System.out.println("Bouton Back");
			System.out.println(this.view.getPanelPrecedent().getName());
			System.out.println(this.view.getPanelCourant().getName());
			this.view.getPanel().removeAll();
			this.view.getPanel().add(this.view.getPanelPrecedent(),BorderLayout.CENTER);
			this.view.getPanel().repaint();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "OK Tri"){
			System.out.println("Bouton OK Tri");
			String tri = this.view.getComboGalerie().getItemAt(this.view.getComboGalerie().getSelectedIndex());

			if (tri.equals("Categorie Alphabetique")){
				System.out.println("Categorie Alphabetique");
			}
			else if(tri.equals("Categorie Non Alphabetique")){
				System.out.println("Categorie Non Alphabetique");
			}
			else if(tri.equals("Auteur Alphabetique")){
				System.out.println("Auteur Alphabetique");

				this.engine.getCollectionSelect().setTriAuteurAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Auteur Non Alphabetique")){
				System.out.println("Auteur Non Alphabetique");

				this.engine.getCollectionSelect().setTriAuteurAntiAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Date Croissante")){
				System.out.println("Date Croissante");

				this.engine.getCollectionSelect().setTriDateCroissante();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Date Decroissante")){
				System.out.println("Date Decroissante");

				this.engine.getCollectionSelect().setTriDateDecroissante();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Pays Alphabetique")){
				System.out.println("Pays Alphabetique");

				this.engine.getCollectionSelect().setTriPaysAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Pays Non Alphabetique")){
				System.out.println("Pays Non Alphabetique");

				this.engine.getCollectionSelect().setTriPaysAntiAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Titre Alphabetique")){
				System.out.println("Titre Alphabetique");

				this.engine.getCollectionSelect().setTriTitreAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
			else if(tri.equals("Titre Non Alphabetique")){
				System.out.println("Titre Non Alphabetique");

				this.engine.getCollectionSelect().setTriTitreAntiAlpha();
				this.view.afficherGalerie(this.engine.getCollectionSelect());


			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Gestion Selection"){
			this.view.afficherGestionSelection();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Changer Theme"){
			this.engine.setTheme(this.view.getComboTheme().getItemAt(this.view.getComboTheme().getSelectedIndex()));
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Defaut Theme"){
			this.engine.setTheme("Defaut");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Ajouter Selection"){
			System.out.println("Bouton ajouter selection");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Aide"){
			this.view.afficherAide();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "A Propos"){
			this.view.afficherAPropos();
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Ajouter Photo"){
			System.out.println("Bouton Ajouter Photo");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Ajouter Categorie"){
			System.out.println("Bouton Ajouter Categorie");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Supprimer Categorie"){
			System.out.println("Bouton Supprimer Categorie");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Supprimer Selection"){
			System.out.println("Bouton Supprimer Selection");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Ajouter Recherche"){
			System.out.println("Bouton Ajouter Recherche");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Edit Selection"){
			System.out.println("Bouton Edition Selection");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Se Connecter"){
			this.engine.setUtilisateurSelect(this.engine.getListeUsers()[this.view.getComboUsers().getSelectedIndex()+1]);
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Se Deconnecter"){
			this.engine.setUtilisateurSelect(this.engine.getListeUsers()[0]);
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand().substring(0, 5).equals("Image")){
			try {
				this.engine.getCollectionSelect().setIndexSelect(source.getActionCommand().substring(5));
			} catch (NoPhotoFoundException e2) {
				e2.printStackTrace();
			}
			try {

				this.view.afficherDiaporama(this.engine.getCollectionSelect().getPhotoSelect());


			} catch (NoPhotoFoundException e1) {
				e1.printStackTrace();
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Previous"){
			try {

				this.engine.getCollectionSelect().prevPhoto();
				this.view.afficherDiaporama(this.engine.getCollectionSelect().getPhotoSelect());


			} catch (NoPhotoFoundException e1) {
				e1.printStackTrace();
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Next"){
			try {

				this.engine.getCollectionSelect().nextPhoto();
				this.view.afficherDiaporama(this.engine.getCollectionSelect().getPhotoSelect());

			} catch (NoPhotoFoundException e1) {
				e1.printStackTrace();
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Bouton Play Pause"){
			if(this.view.getPlaypause().getText().equals("Pause")){
				this.view.getPlaypause().setText("Play");
				this.view.lireDiaporama();
			}
			else{
				this.view.getPlaypause().setText("Pause");
			}
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Ajouter Utilisateur"){
			System.out.println("Bouton Ajouter Utilisateur");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Supprimer Utilisateur"){
			System.out.println("Bouton Supprimer Utilisateur");
		}
		//-------------------------------------------------------------
		else if(source.getActionCommand() == "Similarite"){
			this.engine.getCollectionSelect().setTriSimilarite(this.engine.getCollectionSelect().getIndexSelect());
			this.view.afficherGalerie(this.engine.getCollectionSelect());
		}
	} // ---------------------------------------------------------- actionPerformed()



} // ---------------------------------------------------------- Class AppCtrl
