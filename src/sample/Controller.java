package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.TreeView;

import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;

public class Controller {
    @FXML
    private AnchorPane dialog;
    @FXML
    private TextField txtroot;
    @FXML
    private TreeView<String> view;
    @FXML
    private ImageView itemview;

    //btnpath click event
    public void getpath(ActionEvent event) {
        final DirectoryChooser dc = new DirectoryChooser();   //Create FileBrowser for Directory chose
        Stage dia = (Stage) dialog.getScene().getWindow();          //New Stage (Neues Fenster)
        File dir = dc.showDialog(dia);

        if (dir != null) {                                           //Check if item is selected or selected item is not empty
            txtroot.setText(dir.getAbsolutePath());                 //print selected path to textbox
            //System.out.println(dir.getAbsolutePath());
            String path = dir.getAbsolutePath();
            File file = new File(path);
            //File[] fileArray = file.listFiles();
            //new Controller().getNodesForDirectory(new File(path));
            view.setRoot(getNodesForDirectory(dir));
        }
    }

    public TreeItem<String> getNodesForDirectory(File path) {           //Returns a TreeItem representation of the specified directory
        TreeItem<String> root = new TreeItem<String>(path.getName());
        for (File f : path.listFiles()) {
            System.out.println("Loading " + f.getName());
            if (f.isDirectory()) {                                       //call the function recursively to put directories and files into TreeView
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }
    public String getitempath() {                                       //get path from selected TreeView item and return path type string
        StringBuilder pathBuilder = new StringBuilder();                //create StringBuilder to get the path from type string
        for (TreeItem<String> item = view.getSelectionModel().getSelectedItem();
             item != null; item = item.getParent()) {                   // selected item may not be empty

            pathBuilder.insert(0, item.getValue());
            pathBuilder.insert(0, "/");
        }
        String path = pathBuilder.toString();
        System.out.println(path);                                       //write path to console (only checking)
        if (path.contains(".pdf")){                                     //check if selected item is a PDF file
            createImagesFromPDF(path);
        }
        else{                                                           //if pdf = false call getimage() method
            getimage(path);
        }
        return path;
    }
    public void getimage(String path){                                  //create image from TreeView selected item
        Image I = new Image("file:"+path);
        itemview.setImage(I);
    }

    private List<BufferedImage> createImagesFromPDF(String path){       //convert PDF File to an Image using PDFBox | receives the path of type string

        List<BufferedImage> images = new ArrayList<BufferedImage>();                //create array to write the converted pdf pages as image into the array
        PDDocument pdf = null;
        try {
            InputStream is = new FileInputStream(path);                 //InputStream to get the File (PDF File) from path
            pdf = PDDocument.load(is);                                  //pdf requires type File which got from InputStream
            PDFRenderer pdfRenderer = new PDFRenderer(pdf);
            for (int page = 0; page < pdf.getNumberOfPages(); ++page) {                                 //Create PDF Renderer (PDFBox) and render all PDF Pages to BufferedImages
                BufferedImage img = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                images.add(img);                                                                        //add buffered to the created array list
                WritableImage writable = SwingFXUtils.toFXImage(images.get(page), null);                //create a writable image from buffered image, to write it to ImageView
                itemview.setImage(writable);                                                            //set image to ImageView
                System.out.println(images.size());
            }
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
}