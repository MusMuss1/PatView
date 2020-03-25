package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.TreeView;

import java.io.File;

public class Controller {
    @FXML
    private AnchorPane dialog;
    @FXML
    private TextField txtroot;
    @FXML
    private Button btnpath;
    @FXML
    private TreeView<String> view;
    @FXML
    ImageView itemview;

    //btnpath click event
    public void getpath(ActionEvent event) {
        final DirectoryChooser dc = new DirectoryChooser();   //Create FileBrowser for Directory chose
        Stage dia = (Stage) dialog.getScene().getWindow();          //New Stage (Neues Fenster)
        File dir = dc.showDialog(dia);

        if (dir != null) {                                           //Check if item is selected or selected item is not empty
            txtroot.setText(dir.getAbsolutePath());                 //print selected path to textbox
            //System.out.println(f.getAbsolutePath());
            String path = dir.getAbsolutePath();
            File file = new File(path);
            File[] fileArray = file.listFiles();
            new Controller().getNodesForDirectory(new File(path));
            view.setRoot(getNodesForDirectory(dir));
        }
    }

    public TreeItem<String> getNodesForDirectory(File path) {           //Returns a TreeItem representation of the specified directory
        TreeItem<String> root = new TreeItem<String>(path.getName());
        for (File f : path.listFiles()) {
            System.out.println("Loading " + f.getName());
            if (f.isDirectory()) {                                       //call the function recursively
                root.getChildren().add(getNodesForDirectory(f));
            } else {
                root.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return root;
    }
    public void getitempath() {
        StringBuilder pathBuilder = new StringBuilder();
        for (TreeItem<String> item = view.getSelectionModel().getSelectedItem();
             item != null; item = item.getParent()) {

            pathBuilder.insert(0, item.getValue());
            pathBuilder.insert(0, "/");
        }


    }
}