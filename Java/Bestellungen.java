/*

	Kleiner Wahrenkorb


*/

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Bestellungen extends HttpServlet{

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		String sqlUsr  = "dw54";
		String sqlPswd = "";

		HttpSession session = req.getSession();
	
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String userID    = (String) session.getAttribute("uid");
		String userRolle = (String) session.getAttribute("urolle");

		String funktion     = req.getParameter("f");
		String bestellungID = req.getParameter("bid");

		try{ Class.forName("org.gjt.mm.mysql.Driver"); }
		catch (ClassNotFoundException e){ out.println("DB-Treiber nicht da!"); }

		/*	Es wird kurz ueberpruefft, ob ein Benutzer schon eingeloggt ist
			Da er sich dann nicht mehr registrieren darf */
		String logCheck = "";
		logCheck = (String) session.getAttribute("iEingeloggt");
		if(logCheck.equals("true")) {
			try{
				
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+sqlUsr, sqlUsr, sqlPswd);

				Statement st1 = con.createStatement();

				// Suche einen Warenkorb raus (Status = 0)

				ResultSet rs1;

				boolean admin = false;
				if((userRolle.equals("1"))&&(funktion.equals("list"))){
					admin = true;
					rs1 = st1.executeQuery("SELECT * FROM bestellungen");
				}
				else if((userRolle.equals("1"))&&(funktion.equals("versenden"))){
					admin = true;
					rs1 = st1.executeQuery("SELECT * FROM `bestellungen` WHERE `id` LIKE '"+bestellungID+"'");
				}
				else rs1 = st1.executeQuery("SELECT * FROM `bestellungen` WHERE `kundeID` LIKE '"+userID+"' AND `status` NOT LIKE '0'");



				boolean keinEintrag = true;
				int     anzahlWare  = 0;
				int     summeWare   = 0;
				double  summe       = 0.0;

				String ausgabe = "";

				if((admin)&&(funktion.equals("versenden"))){
					Statement st2 = con.createStatement();

					int i = st.executeUpdate("UPDATE `bestellungen` SET `status` = '3' WHERE `bestellungen`.`id` ="+bestellungID);
					ausgabe = "Status: "+i;
					
				}
				if((admin)&&(funktion.equals("loeschen"))){ //////////////////////////////// TODO
					Statement st2 = con.createStatement();

					int i = st.executeUpdate("UPDATE `bestellungen` SET `status` = '3' WHERE `bestellungen`.`id` ="+bestellungID);
					ausgabe = "Status: "+i;
					
				}
				else{
					while(rs1.next()){

						String bestID = rs1.getString("id");



						ausgabe = ausgabe+"\n<div class='bestellung'><form method='POST' action='http://praxi.mt.haw-hamburg.de/~dw54/servlet/Bestellungen'>Bestellung ID="+bestID+"<br>\n";


						int bStatus = rs1.getInt("status");

						if(admin) ausgabe = ausgabe+"UserID: "+rs1.getString("kundeID");

						if(bStatus == 0)	ausgabe = ausgabe+"<br>\nAdresse: <input type='text' name='adresse'><br>";
						else				ausgabe = ausgabe+"<br>\nAdresse: "+rs1.getString("adresse")+"<br>";

						if(admin) ausgabe = ausgabe+"\n<br><br><a href='http://praxi.mt.haw-hamburg.de/~dw54/servlet/Bestellungen?f=versenden&id="+bestID+"'>Versenden</a> <a href='http://praxi.mt.haw-hamburg.de/~dw54/servlet/Bestellungen?f=loeschen&id="+bestID+"'>L&ouml;schen</a>\n<br><br>";


						if     (bStatus == 0) ausgabe = ausgabe+"Status: Warenkorb<br>";
						else if(bStatus == 1) ausgabe = ausgabe+"Status: Bestellt<br>";
						else if(bStatus == 2) ausgabe = ausgabe+"Status: In Bearbeitung<br>";
						else if(bStatus == 3) ausgabe = ausgabe+"Status: Versendet<br>";
						else 				  ausgabe = ausgabe+"Status: Unbekannt<br>";

						ausgabe = ausgabe + "\n<br><table border='0'><tr><th>Position</th><th>Bezeichnung</th><th>Anzahl</th><th>Preis</th><th>Betrag</th><th>L&ouml;schen</th></tr>";

						Statement st2 = con.createStatement();

						ResultSet rs2 = st2.executeQuery("SELECT * FROM bestWare WHERE bestellungID LIKE '"+bestID+"'");

						while(rs2.next()){

							int anzahl    = rs2.getInt("anzahl");
							anzahlWare    = anzahlWare+1;
							summeWare     = summeWare+anzahl;

							String wareID = rs2.getString("wareID");

							Statement st3 = con.createStatement();

							ResultSet rs3 = st3.executeQuery("SELECT * FROM ware WHERE id LIKE '"+wareID+"'");
							double preis = 0;
							String bezeichnung = "";
							while(rs3.next()){
								preis = rs3.getDouble("preis");
								bezeichnung = rs3.getString("name");
							}
							st3.close();
							String position = rs2.getString("position");

							ausgabe = ausgabe+"\n<tr><th>"+position+"</th><th>"+bezeichnung+"</th><th>"+(bStatus == 0 ? "<input type='text' name='name' value='" :"")+anzahl+(bStatus == 0 ? "'>" :"")+"</th><th>"+preis+" &euro;</th><th>"+(anzahl*preis)+" &euro;</th><th>"+(bStatus == 0 ? "<input type='checkbox' name='"+bestID+"' value='"+position+"'>" : "&nbsp;")+"</th></tr>";

							summe = summe+(anzahl*preis);
							keinEintrag = false;
						}
						st2.close();
						ausgabe = ausgabe+"\n<tr><th>&nbsp;</th><th>&nbsp;</th><th>&nbsp;</th><th>&nbsp;</th><th>&nbsp;</th><th>&nbsp;</th></tr>";
						ausgabe = ausgabe+"\n<tr><th>Summe</th><th>&nbsp;</th><th>"+summeWare+"</th><th>&nbsp;</th><th>"+summe+" &euro;</th><th>&nbsp;</th></tr>";

						ausgabe = ausgabe+"</table>";


						//if(admin) ausgabe = ausgabe+"\n<input type='submit' value='L&ouml;schen' name='loeschen'><input type='submit' value='Versendet' name='versendet'>";
						if(bStatus == 0)  ausgabe = ausgabe+"\n<input type='submit' value='&Auml;ndern' name='aendern'><input type='submit' value='Bestellen' name='bestellen'>";

						ausgabe = ausgabe+"</form></div>";
					}
				}

				out.println(ausgabe);

				// if(keinEintrag) out.println("Du hast nichts im Warenkorb");
				// else			out.println("<a href='index.jsp?p=wagen'>Du hast "+anzahlWare+" Produkte ("+summeWare+" insg.) f&uuml;r "+summe+" &euro; im Warenkorb</a>");
				
				st1.close();
				con.close();
			}
			catch (Exception e){
				out.println(" MySQL Exception: " + e.getMessage());
			}
		}
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		doPost(req,res);
	}
}
