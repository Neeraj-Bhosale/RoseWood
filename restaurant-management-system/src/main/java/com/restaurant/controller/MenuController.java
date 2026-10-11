package com.restaurant.controller;

import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.service.MenuService;
import com.restaurant.util.AlertUtil;
import com.restaurant.util.CurrencyUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MenuController {

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, Integer> colId;
    @FXML private TableColumn<MenuItem, String> colCategory;
    @FXML private TableColumn<MenuItem, String> colName;
    @FXML private TableColumn<MenuItem, Double> colPrice;
    @FXML private TableColumn<MenuItem, Integer> colPrepTime;
    @FXML private TableColumn<MenuItem, Boolean> colAvailable;

    @FXML private ComboBox<MenuCategory> categoryComboBox;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField prepTimeField;
    @FXML private TextArea descriptionArea;
    @FXML private CheckBox availableCheckBox;

    @FXML private TextField searchField;
    @FXML private ComboBox<MenuCategory> filterCategoryBox;

    private final MenuService menuService = new MenuService();
    private final ObservableList<MenuItem> menuItemsList = FXCollections.observableArrayList();
    private MenuItem selectedItem;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(CurrencyUtil.forTableColumn());
        colPrepTime.setCellValueFactory(new PropertyValueFactory<>("preparationTime"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        loadCategories();

        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedItem = newVal;
                nameField.setText(newVal.getName());
                priceField.setText(String.valueOf(newVal.getPrice()));
                prepTimeField.setText(String.valueOf(newVal.getPreparationTime()));
                descriptionArea.setText(newVal.getDescription());
                availableCheckBox.setSelected(newVal.isAvailable());

                for (MenuCategory cat : categoryComboBox.getItems()) {
                    if (cat.getCategoryId() == newVal.getCategoryId()) {
                        categoryComboBox.setValue(cat);
                        break;
                    }
                }
            }
        });

        loadMenuItems();
    }

    private void loadCategories() {
        List<MenuCategory> categories = menuService.getAllCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));

        ObservableList<MenuCategory> filterList = FXCollections.observableArrayList();
        filterList.add(new MenuCategory(0, "All Categories", "", 0));
        filterList.addAll(categories);
        filterCategoryBox.setItems(filterList);
        filterCategoryBox.setValue(filterList.get(0));
    }

    private void loadMenuItems() {
        menuItemsList.clear();
        menuItemsList.addAll(menuService.getAllMenuItems());
        menuTable.setItems(menuItemsList);
    }

    @FXML
    public void handleAddItem() {
        try {
            MenuCategory cat = categoryComboBox.getValue();
            if (cat == null) {
                AlertUtil.showWarning("Missing Category", "Please select a menu category.");
                return;
            }
            double price = Double.parseDouble(priceField.getText().trim());
            int prepTime = Integer.parseInt(prepTimeField.getText().trim());

            boolean success = menuService.addMenuItem(
                    cat.getCategoryId(),
                    nameField.getText(),
                    descriptionArea.getText(),
                    price,
                    prepTime,
                    availableCheckBox.isSelected()
            );

            if (success) {
                AlertUtil.showInfo("Success", "Menu item added successfully.");
                clearForm();
                loadMenuItems();
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Number", "Price and Prep Time must be valid numeric values.");
        } catch (Exception e) {
            AlertUtil.showError("Save Failed", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateItem() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Selection", "Please select an item to edit.");
            return;
        }
        try {
            MenuCategory cat = categoryComboBox.getValue();
            if (cat != null) selectedItem.setCategoryId(cat.getCategoryId());
            selectedItem.setName(nameField.getText());
            selectedItem.setPrice(Double.parseDouble(priceField.getText().trim()));
            selectedItem.setPreparationTime(Integer.parseInt(prepTimeField.getText().trim()));
            selectedItem.setDescription(descriptionArea.getText());
            selectedItem.setAvailable(availableCheckBox.isSelected());

            boolean success = menuService.updateMenuItem(selectedItem);
            if (success) {
                AlertUtil.showInfo("Success", "Menu item updated.");
                clearForm();
                loadMenuItems();
            }
        } catch (Exception e) {
            AlertUtil.showError("Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteItem() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Selection", "Please select an item to delete.");
            return;
        }
        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete " + selectedItem.getName() + "?");
        if (confirm) {
            boolean success = menuService.deleteMenuItem(selectedItem.getItemId());
            if (success) {
                AlertUtil.showInfo("Success", "Menu item deleted.");
                clearForm();
                loadMenuItems();
            }
        }
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText();
        MenuCategory cat = filterCategoryBox.getValue();
        Integer catId = (cat != null && cat.getCategoryId() > 0) ? cat.getCategoryId() : null;

        List<MenuItem> results = menuService.searchAndFilter(query, catId, false);
        menuTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    public void handleSortByPrice() {
        String query = searchField.getText();
        MenuCategory cat = filterCategoryBox.getValue();
        Integer catId = (cat != null && cat.getCategoryId() > 0) ? cat.getCategoryId() : null;

        List<MenuItem> results = menuService.searchAndFilter(query, catId, true);
        menuTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    public void clearForm() {
        selectedItem = null;
        nameField.clear();
        priceField.clear();
        prepTimeField.clear();
        descriptionArea.clear();
        availableCheckBox.setSelected(true);
        categoryComboBox.getSelectionModel().clearSelection();
        menuTable.getSelectionModel().clearSelection();
    }
}
