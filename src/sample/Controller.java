package sample;

// https://github.com/MusMuss1/PatView

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.TreeView;
import javafx.scene.control.Label;

import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;

public class Controller {
    @FXML
    private TextField txtroot;
    @FXML
    private TreeView<String> view;
    @FXML
    private ImageView itemview;
    @FXML
    ScrollPane pane = new ScrollPane();
    @FXML
    Label lblpage = new Label();
    @FXML
    TextField txtpage = new TextField();

    private String path;
    private String dirpath;
    private int index = 0;                                                                                                  //index type Integer saves PDF page number
    final ArrayList<Image> pics = new ArrayList<>();                                                                        //create ArrayList<Image> Type

    //btnpath click event
    public void getpath(ActionEvent event) {
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
        TreeItem<String> root = new TreeItem<String>(dir.getName());
        for (File f : dir.listFiles()) {
            System.out.println("Loading " + f.getName());                                                                   //write directories to console (only checking)
            if (f.isDirectory()) {                                                                                          //call the function recursively to put directories and files into TreeView
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }
    public String getitempath() {                                                                                           //get path from selected TreeView item and return path type string
        StringBuilder pathBuilder = new StringBuilder();                                                                    //create StringBuilder to get the path from type string
        for (TreeItem<String> item = view.getSelectionModel().getSelectedItem();                                            //get ItemValue for every TreeItem
             item != null; item = item.getParent()) {                                                                       //selected item may not be empty

            pathBuilder.insert(0, item.getValue());                                                                   //building path
            pathBuilder.insert(0, "\\");
        }
        path = dirpath;                                                                                                      //set path = root directory -> first part of AbsolutePath
        path = path.replace("\\"+view.getRoot().getValue(),"")+pathBuilder.toString();                      //delete TreeRootItem cause it already exists in dirpath
        System.out.println("Selected Document: "+path);                                                                     //write path to console (only checking)
        if (path.contains(".pdf")){                                                                                         //check if selected item is a PDF file
            createImagesFromPDF(path);
        }
        else{                                                                                                               //if pdf = false call getimage() method
            getimage(path);
        }
        return path;
    }
    public void getimage(String path){                                                                                      //create image from TreeView selected item
        Image I = new Image("file:"+path);
        itemview.setImage(I);
    }

    private List<BufferedImage> createImagesFromPDF(String path){                                                           //convert PDF File to an Image using PDFBox | receives the path of type string

        List<BufferedImage> images = new ArrayList<BufferedImage>();                                                                    //create array to write the converted pdf pages as image into the array
        pics.clear();                                                                                                       //clear pics ArrayList<Image> to avoid problems with displaying PDF File in ImageView
        PDDocument pdf = null;
        index = 0;                                                                                                          //set index to 0 to display 1st page of the PDF File when clicking on it
        try {
            InputStream is = new FileInputStream(path);                                                                     //InputStream to get the File (PDF File) from path
            pdf = PDDocument.load(is);                                                                                      //pdf requires type File which got from InputStream
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            for (int page = 0; page < pdf.getNumberOfPages(); ++page) {                                                     //Create PDF Renderer (PDFBox) and render all PDF Pages to BufferedImages
                BufferedImage img = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                images.add(img);                                                                                            //add buffered to the created array list
                WritableImage writable = SwingFXUtils.toFXImage(images.get(page), null);                        //create a writable image from buffered image, to write it to ImageView
                pics.add(writable);                                                                                         //add writable image to new ArrayList "pics"
                itemview.setImage(pics.get(index));                                                                         //set image to ImageView
                pane.setContent(itemview);
                settxtpage();
                System.out.println(images.size());                                                                          //print size of pics ArrayList (only checking)
            }
            is.close();                                                                                                     //important to close InputStream if we want to delete a selected file later
        }
        catch (IOException e) {
            e.printStackTrace();
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
    public void goforward(ActionEvent event){                                                                               //"goforwad" method handles btnforward click event
        try{
            if(index<pics.size()-1){
                index++;
                itemview.setImage(pics.get(index));
            }else{
                index = 0;
                itemview.setImage(pics.get(index));
            }
        }catch (Exception e){

        }
        settxtpage();
    }
    //btnback click event
    public void goback(ActionEvent event){                                                                                  //"goback" mehtod handles btnback click event
        try{
            if(index>0){
                index--;
                itemview.setImage(pics.get(index));
            }else{
                index=pics.size();
                index--;
                itemview.setImage(pics.get(index));
            }
        }catch (Exception e){

        }
        settxtpage();
    }
    public void zoomIn() {                                                                                                 //Zoom PDF or picture (zooming not scaling)
        itemview.setFitHeight(itemview.getFitHeight()*1.1);
        itemview.setFitWidth(itemview.getFitWidth()*1.1);
        pane.setPannable(true);
    }
    public void zoomOut() {
        itemview.setFitHeight(itemview.getFitHeight()/1.1);
        itemview.setFitWidth(itemview.getFitWidth()/1.1);
        pane.setPannable(true);
    }
    public void reset(){
        pane.setFitToHeight(true);
        itemview.setFitHeight(545);
        itemview.setFitWidth(608);
    }
    public void settxtpage(){                                                                                               //set selected page to TextField (+1 because page index may not <= 0, there is no page "0")
        txtpage.setText(String.valueOf(index+1));
        lblpage.setText("/"+ String.valueOf(pics.size()));
    }
    public void changepage(ActionEvent chp) {                                                                              //set "txtpage" TextField to index
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
                Alert del = new Alert(Alert.AlertType.CONFIRMATION, "Die angeforderte Seite exsistiert nicht .", ButtonType.OK);
                del.show();
                txtpage.setText(String.valueOf(1));
            }
        }
    }

    public void remove(){
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
                // File permission problems are caught here.
                System.err.println(x);
            }
        }
    }
}