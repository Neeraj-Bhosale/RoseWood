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
                if (categoryComboBox.getItems().isEmpty()) {
                    MenuCategory defaultCat = new MenuCategory(1, "Main Dishes", "Standard Menu Items", 1);
                    new com.restaurant.repository.MenuRepository().createCategory(defaultCat);
                    loadCategories();
                    cat = defaultCat;
                    categoryComboBox.setValue(defaultCat);
                } else {
                    AlertUtil.showWarning("Missing Category", "Please select a menu category.");
                    return;
                }
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

    @FXML
    public void handleImportDummyData() {
        try {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            int count = dds.importDummyDishes();
            AlertUtil.showInfo("Dummy Dishes Imported", "Successfully imported " + count + " dishes with complete ingredient recipes.");
            loadCategories();
            loadMenuItems();
        } catch (Exception e) {
            AlertUtil.showError("Import Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVacateData() {
        boolean confirm = AlertUtil.showConfirmation(
                "Vacate Menu Dishes",
                "Are you sure you want to clear all dishes and categories? The menu catalog will become vacant."
        );
        if (confirm) {
            com.restaurant.service.DummyDataService dds = new com.restaurant.service.DummyDataService();
            dds.vacateDishes();
            clearForm();
            loadCategories();
            loadMenuItems();
        }
    }

    @FXML
    public void handleManageRecipe() {
        if (selectedItem == null) {
            AlertUtil.showWarning("No Dish Selected", "Please select a dish from the menu table to view or configure its ingredients.");
            return;
        }

        com.restaurant.service.MenuRecipeService recipeService = new com.restaurant.service.MenuRecipeService();
        com.restaurant.service.InventoryService inventoryService = new com.restaurant.service.InventoryService();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Recipe Ingredients — " + selectedItem.getName());
        dialog.setHeaderText("Map Kitchen Inventory Ingredients to " + selectedItem.getName() + "\nQuantities will be deducted live from inventory whenever this dish is ordered.");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        TableView<com.restaurant.model.MenuItemIngredient> recipeTable = new TableView<>();
        recipeTable.setPrefHeight(200);

        TableColumn<com.restaurant.model.MenuItemIngredient, String> ingNameCol = new TableColumn<>("Ingredient");
        ingNameCol.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        ingNameCol.setPrefWidth(220);

        TableColumn<com.restaurant.model.MenuItemIngredient, Double> ingQtyCol = new TableColumn<>("Portion Qty");
        ingQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantityRequired"));
        ingQtyCol.setPrefWidth(100);

        TableColumn<com.restaurant.model.MenuItemIngredient, String> ingUnitCol = new TableColumn<>("Unit");
        ingUnitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        ingUnitCol.setPrefWidth(80);

        recipeTable.getColumns().addAll(ingNameCol, ingQtyCol, ingUnitCol);

        ObservableList<com.restaurant.model.MenuItemIngredient> recipeList = FXCollections.observableArrayList(
                recipeService.getIngredientsForMenuItem(selectedItem.getItemId())
        );
        recipeTable.setItems(recipeList);

        // Add Ingredient Controls
        ComboBox<com.restaurant.model.InventoryItem> invCombo = new ComboBox<>();
        invCombo.setPromptText("Select Ingredient...");
        invCombo.setItems(FXCollections.observableArrayList(inventoryService.getAllInventory()));
        invCombo.setPrefWidth(200);

        TextField qtyField = new TextField();
        qtyField.setPromptText("Qty (e.g. 0.25)");
        qtyField.setPrefWidth(110);

        Button btnAddIng = new Button("+ Add Ingredient");
        btnAddIng.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAddIng.setOnAction(ev -> {
            com.restaurant.model.InventoryItem selectedInv = invCombo.getValue();
            if (selectedInv == null) {
                AlertUtil.showWarning("Missing Ingredient", "Please select an inventory ingredient.");
                return;
            }
            try {
                double q = Double.parseDouble(qtyField.getText().trim());
                if (q <= 0) {
                    AlertUtil.showWarning("Invalid Quantity", "Quantity must be greater than zero.");
                    return;
                }
                recipeService.addIngredientToDish(selectedItem.getItemId(), selectedInv.getInventoryId(), q, selectedInv.getUnit());
                recipeList.clear();
                recipeList.addAll(recipeService.getIngredientsForMenuItem(selectedItem.getItemId()));
                qtyField.clear();
            } catch (NumberFormatException nfe) {
                AlertUtil.showError("Invalid Number", "Please enter a valid numeric quantity (e.g. 0.25).");
            }
        });

        Button btnDeleteIng = new Button("Remove Selected");
        btnDeleteIng.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
        btnDeleteIng.setOnAction(ev -> {
            com.restaurant.model.MenuItemIngredient sel = recipeTable.getSelectionModel().getSelectedItem();
            if (sel != null && sel.getRecipeId() > 0) {
                recipeService.removeIngredient(sel.getRecipeId());
                recipeList.remove(sel);
            }
        });

        javafx.scene.layout.HBox addBox = new javafx.scene.layout.HBox(10, invCombo, qtyField, btnAddIng);
        addBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        javafx.scene.layout.VBox dialogContent = new javafx.scene.layout.VBox(12,
                new Label("Current Recipe Ingredients:"),
                recipeTable,
                btnDeleteIng,
                new Separator(),
                new Label("Add Ingredient to Recipe:"),
                addBox
        );
        dialogContent.setPadding(new javafx.geometry.Insets(15));
        dialog.getDialogPane().setContent(dialogContent);
        dialog.showAndWait();
    }
}
