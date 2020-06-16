package sample;

// https://github.com/MusMuss1/PatView

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.TreeView;

import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;

public class Controller implements Initializable {
    @FXML
    private TextField txtroot;
    @FXML
    private TreeView<String> view;
    @FXML
    private ImageView itemview;
    @FXML
    ScrollPane pane = new ScrollPane();
    @FXML
    Pane control2 = new Pane();
    @FXML
    Pane control1 = new Pane();
    @FXML
    Label lblpage = new Label();
    @FXML
    Label lblinfo = new Label();
    @FXML
    TextField txtpage = new TextField();
    @FXML
    ListView thumbview = new ListView();
    @FXML
    SplitPane spane = new SplitPane();
    @FXML
    public Button btnpath = new Button();
    public Button btnforward = new Button();
    public Button btnback = new Button();
    public Button btnfirst = new Button();
    public Button btnlast = new Button();
    public Button btnplus = new Button();
    public Button btnminus = new Button();
    public Button btnreset = new Button();
    public Button btndelete = new Button();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {                                                        //initialize and set size of ImageView to ScrollPane size

        assert txtroot != null : "fx:id=\"btnback\" was not injected: Check your FXML file!";
        assert view != null : "fx:id=\"myImage\" was not injected: Check your FXML file!";
        assert itemview != null : "fx:id=\"itemview\" was not injected: Check your FXML file!";
        assert pane != null : "fx:id=\"pane\" was not injected: Check your FXML file!";
        assert control2 != null : "fx:id=\"control2\" was not injected: Check your FXML file!";
        assert control1 != null : "fx:id=\"control1\" was not injected: Check your FXML file!";
        assert lblpage != null : "fx:id=\"lblpage\" was not injected: Check your FXML file!";
        assert lblinfo != null : "fx:id=\"lblinfo\" was not injected: Check your FXML file!";
        assert txtpage != null : "fx:id=\"txtpage\" was not injected: Check your FXML file!";
        assert thumbview != null : "fx:id=\"thumbview\" was not injected: Check your FXML file!";
        assert spane != null : "fx:id=\"spane\" was not injected: Check your FXML file!";

        assert btnpath != null : "fx:id=\"btnpath\" was not injected: Check your FXML file!";
        assert btnforward != null : "fx:id=\"btnforward\" was not injected: Check your FXML file!";
        assert btnback != null : "fx:id=\"btnback\" was not injected: Check your FXML file!";
        assert btnfirst != null : "fx:id=\"btnfirst\" was not injected: Check your FXML file!";
        assert btnlast != null : "fx:id=\"btnlast\" was not injected: Check your FXML file!";
        assert btnplus != null : "fx:id=\"btnplus\" was not injected: Check your FXML file!";
        assert btnminus != null : "fx:id=\"btnminus\" was not injected: Check your FXML file!";
        assert btnreset != null : "fx:id=\"btnreset\" was not injected: Check your FXML file!";
        assert btndelete != null : "fx:id=\"btndelete\" was not injected: Check your FXML file!";

        btnpath.setTooltip(new Tooltip("Durchsuchen"));
        btnforward.setTooltip(new Tooltip("Vor"));
        btnback.setTooltip(new Tooltip("Zurück"));
        btnfirst.setTooltip(new Tooltip("Erste Seite"));
        btnlast.setTooltip(new Tooltip("Letzte Seite"));
        btnplus.setTooltip(new Tooltip("Hineinzoomen"));
        btnminus.setTooltip(new Tooltip("Herauszoomen"));
        btnreset.setTooltip(new Tooltip("Zoom zurücksetzen"));
        btndelete.setTooltip(new Tooltip("Löschen"));
        txtpage.setTooltip(new Tooltip("Seite eintragen"));

        if (pane != null) {
            pane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    itemview.setFitHeight(pane.getHeight());
                    itemview.setFitWidth(pane.getWidth());
                }
            });
            pane.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    itemview.setFitHeight(pane.getHeight());
                    itemview.setFitWidth(pane.getWidth());
                }
            });
        }
    }


    private String path;
    private String dirpath;
    private int index = 0;                                                                                                  //index type Integer saves PDF page number
    final ArrayList<Image> pics = new ArrayList<>();                                                                        //create ArrayList<Image> Type

    //get src
    Image rootIcon = new Image("root.png");
    Image Image = new Image("image.png");
    Image PDF = new Image("pdf.png");
    Image noFile = new Image("nofile.png");
    Image noFilebig = new Image("nofilebig.png");


    //btnpath click event
    public void getpath(ActionEvent gp) {
        final DirectoryChooser dc = new DirectoryChooser();                                                                 //Create FileBrowser for Directory chose
        File dir = dc.showDialog(null);

        if (dir != null) {                                                                                                  //Check if item is selected or selected item is not empty
            dirpath = dir.getAbsolutePath();
            txtroot.setText(dirpath);                                                                                       //print selected path to TextBox
            //System.out.println(dir.getAbsolutePath());
            view.setRoot(getNodesForDirectory(dir));
        }
    }

    public TreeItem<String> getNodesForDirectory(File dir) {                                                                //Returns a TreeItem representation of the specified directory
        TreeItem<String> root = new TreeItem<String>(dir.getName(),new ImageView(rootIcon));
        for (File f : dir.listFiles()) {
            System.out.println("Loading " + f.getName());                                                                   //write directories to console (only checking)
            if (f.isDirectory()) {                                                                                          //call the function recursively to put directories and files into TreeView
                root.getChildren().add(getNodesForDirectory(f));
            } else if (f.getName().endsWith(".pdf")) {
                root.getChildren().add(new TreeItem<String>(f.getName(),new ImageView(PDF)));
            } else if (f.getName().endsWith(".png")|f.getName().endsWith(".jpg")) {
                root.getChildren().add(new TreeItem<String>(f.getName(),new ImageView(Image)));
            } else
                root.getChildren().add(new TreeItem<String>(f.getName(),new ImageView(noFile)));
        }
        return root;
    }

    public String getitempath() {                                                                                           //get path from selected TreeView item and return path type string
        if (view.getSelectionModel().getSelectedItem() != null){                                                            //check if user has selected a TreeItem to avoid exception
            pane.setDisable(false);
            lblinfo.setVisible(true);

            StringBuilder pathBuilder = new StringBuilder();                                                                //create StringBuilder to get the path from type string
            for (TreeItem<String> item = view.getSelectionModel().getSelectedItem();                                        //get ItemValue for every TreeItem
                 item != null&&item !=view.getRoot(); item = item.getParent()) {                                            //selected item may not be empty and not RootItem to avoid double root in path

                pathBuilder.insert(0, item.getValue());                                                               //building path
                pathBuilder.insert(0, File.separator);
            }

            path = dirpath + pathBuilder.toString();                                                                        //merge dirpath and nodes from TreeView
            System.out.println("Selected Document: "+path);                                                                 //write path to console (only checking)
            lblinfo.setText("Patient: "+view.getSelectionModel().getSelectedItem().getValue());                             //show Patient ID

            if (path.endsWith(".pdf")){                                                                                     //check if selected item is a PDF file
                reset();                                                                                                    //reset imagezoom
                createImagesFromPDF(path);
                control1.setVisible(true);
                control2.setVisible(true);
                showthumblist();

                if (pics.size()<=1) {
                    control2.setDisable(true);
                }
                else {
                    control2.setDisable(false);
                }
                lblinfo.setText("Patient: "+view.getSelectionModel().getSelectedItem().getParent().getValue()+" || "+"Dokument: "+view.getSelectionModel().getSelectedItem().getValue());
            }
            else if(path.endsWith(".png")|path.endsWith(".jpg")){                                                           //if image call getimage() method + hide unneeded controls
                reset();
                getimage(path);
                control1.setVisible(true);
                control2.setVisible(false);                                                                                 //displaying page is not need for images
                lblinfo.setText("Patient: "+view.getSelectionModel().getSelectedItem().getParent().getValue()+" || "+"Dokument: "+view.getSelectionModel().getSelectedItem().getValue());
                hidethumblist();
            }else{
                otherfile();
            }
        }
        return path;
    }

    public void otherfile(){
        reset();																											//what we do if a folder is selected | hide all controls
        itemview.setImage(null);
        control1.setVisible(false);
        control2.setVisible(false);
        hidethumblist();
        pane.setDisable(true);

        //important if we have an unsupported file
        File f = new File(path);																							//check if selected item is a file and not a folder(patient folder)
        if (f.isFile()){
            itemview.setImage(noFilebig);
            itemview.setFitHeight(itemview.getFitHeight()*0.3);
            itemview.setFitWidth(itemview.getFitWidth()*0.3);
            pane.setDisable(true);
            lblinfo.setText("Fehler: Dateiformat wird nicht unterstützt, bitte benutzen Sie nur Dateien vom Typ (.jpg | .png | .pdf) !"); //display message, to user
        }
    }

    public void getimage(String path){                                                                                      //create image from TreeView selected item
        File picture = new File(path);
        if(picture.isFile()){                                                                                               //check if the requested file exists
            Image I = new Image(picture.toURI().toString());                                                                //set selected file to ImageView
            itemview.setImage(I);
        }
    }

    private List<BufferedImage> createImagesFromPDF(String path){                                                           //convert PDF File to an Image using PDFBox | receives the path of type string

        List<BufferedImage> images = new ArrayList<BufferedImage>();                                                        //create array to write the converted pdf pages as image into the array
        pics.clear();                                                                                                       //clear pics ArrayList<Image> to avoid problems with displaying PDF File in ImageView
        PDDocument pdf = null;
        index = 0;                                                                                                          //set index to 0 to display 1st page of the PDF File when clicking on it
        try {
            InputStream is = new FileInputStream(path);                                                                     //InputStream to get the File (PDF File) from path
            pdf = PDDocument.load(is);                                                                                      //pdf requires type File which got from InputStream
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            for (int page = 0; page < pdf.getNumberOfPages(); ++page) {                                                     //Create PDF Renderer (PDFBox) and render all PDF Pages to BufferedImages
                BufferedImage img = pdfRenderer.renderImageWithDPI(page, 200, ImageType.RGB);
                images.add(img);                                                                                            //add buffered to the created array list
                WritableImage writable = SwingFXUtils.toFXImage(images.get(page), null);                        			//create a writable image from buffered image, to write it to ImageView
                pics.add(writable);                                                                                         //add writable image to new ArrayList "pics"
                itemview.setImage(pics.get(index));                                                                         //set image to ImageView
                settxtpage();
                System.out.println(images.size());                                                                          //print size of pics ArrayList (only checking)
            }
            is.close();                                                                                                     //important to close InputStream if we want to delete a selected file later
            setthumb();
        }
        catch (IOException e) {
            e.printStackTrace();
            otherfile();
            lblinfo.setText("Fehler: Die Datei ist möglicherweise defekt.");
        }
        try {
            pdf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }

    //brnforward click event
    public void goforward(ActionEvent gf){                                                                                 //"goforwad" method handles btnforward click event
        try{
            if(index<pics.size()-1){
                index++;
            }else{
                index = 0;
            }
            itemview.setImage(pics.get(index));
        }catch (Exception e){

        }
        settxtpage();
    }

    //btnback click event
    public void goback(ActionEvent gb){                                                                                    //"goback" mehtod handles btnback click event
        try{
            if(index>0){
                index--;
            }else{
                index=pics.size()-1;
            }
            itemview.setImage(pics.get(index));
        }catch (Exception e){

        }
        settxtpage();
    }

    public void golast(){
        index = pics.size()-1;
        itemview.setImage(pics.get(index));
        settxtpage();
    }
    public void gofirst(){
        index = 0;
        itemview.setImage(pics.get(index));
        settxtpage();
    }

    public void zoomIn() {                                                                                                 //Zoom PDF or picture (zooming not scaling)
        itemview.setFitHeight(itemview.getFitHeight()*1.1);
        itemview.setFitWidth(itemview.getFitWidth()*1.1);
    }

    public void zoomOut() {
        itemview.setFitHeight(itemview.getFitHeight()/1.1);
        itemview.setFitWidth(itemview.getFitWidth()/1.1);
    }

    public void ScrollZoom(ScrollEvent event) {                                                                             //ZoomScrollEvent
        double zoomFactor = 1.1;                                                                                            //define zoomfaktor
        double deltaY = event.getDeltaY();                                                                                  //get Scrollingvalue as double for up and down

        if (deltaY < 0){                                                                                                    //if < 0 = scrolling down -> negative zoomfactor to zoom out
            zoomFactor = 0.9;
        }                                                                                                                   //same like zoomIn
        itemview.setFitHeight(itemview.getFitHeight()*zoomFactor);
        itemview.setFitWidth(itemview.getFitWidth()*zoomFactor);
        event.consume();
    }

    public void reset(){
        itemview.setFitHeight(pane.getHeight());
        itemview.setFitWidth(pane.getWidth());
    }

    public void settxtpage(){                                                                                               //set selected page to TextField (+1 because page index may not <= 0, there is no page "0")
        txtpage.setText(String.valueOf(index+1));
        lblpage.setText("/ "+ String.valueOf(pics.size()));
    }

    public void changepage() {                                                                              				//set "txtpage" TextField to index
        if(txtpage.getText().isEmpty()==false) {
            index = Integer.parseInt(txtpage.getText()) - 1;
            if (index >= 0 && index <= pics.size()-1) {                                                                    //-1 because Array is from 0-x
                try {
                    index = Integer.parseInt(txtpage.getText()) - 1;                                                       //set index to ArrayList for displaying right image in ImageView
                    itemview.setImage(pics.get(index));
                } catch (IllegalArgumentException e) {
                    System.out.println(String.valueOf(index) + "Ist keine Seite.");
                }
            }else {
                Alert del = new Alert(Alert.AlertType.WARNING, "Die angeforderte Seite exsistiert nicht .", ButtonType.OK);
                del.show();
                txtpage.setText(String.valueOf(1));
            }
        }
    }

    public void remove(ActionEvent rm){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Soll die Datei unwiderruflich gelöscht werden" + " ?", ButtonType.YES, ButtonType.NO);       //show dialog for user
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try {
                Path delpath = Paths.get(path);                                                                            //path contains TreeView selected item
                Files.delete(delpath);
                TreeItem c = (TreeItem)view.getSelectionModel().getSelectedItem();                                         //after file removed we remove the TreeView selected item
                c.getParent().getChildren().remove(c);
                itemview.setImage(null);                                                                                   //clear ImageView
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", path);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("%s not empty%n", path);
            } catch (IOException x) {
                System.err.println(x);																						// File permission problems
            }
        }
    }

    //write PDF images (pages) from Image Array to ListView
    public void setthumb() {
        thumbview.getItems().clear();
        for (int i = 0; i < pics.size(); i++) {
            ImageView thumb = new ImageView(pics.get(i));
            Label la = new Label(String.valueOf(i+1));
            StackPane sb = new StackPane();
            sb.setAlignment(la, Pos.CENTER_LEFT);
            sb.getChildren().addAll(thumb,la);
            thumb.setFitHeight(192);
            thumb.setFitWidth(128);
            thumbview.getItems().addAll(sb);
        }
    }
    //browse to selected page from ListView
    public void setthumbpage() {
        if(thumbview.getSelectionModel().getSelectedItem() != null){
            txtpage.setText(String.valueOf(thumbview.getSelectionModel().getSelectedIndex()+1));
            System.out.println(thumbview.getSelectionModel().getSelectedIndex()+1);
            changepage();
        }
    }
    //show thumdaillist
    public void showthumblist() {
        thumbview.setVisible(true);
        thumbview.setPrefWidth(192);
        thumbview.setMaxWidth(192);
        thumbview.setMinWidth(192);
    }
    //hide thumbdaillist
    public void hidethumblist() {
        thumbview.setVisible(false);
        thumbview.setMaxWidth(0);
        thumbview.setMinWidth(0);
    }
}