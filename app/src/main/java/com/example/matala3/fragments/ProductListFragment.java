package com.example.matala3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matala3.R;
import com.example.matala3.adapters.ProductAdapter;
import com.example.matala3.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private NavController navController;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewAllProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this::addProductToCart);
        recyclerView.setAdapter(productAdapter);

        // ✅ Get NavController
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Fetch products from Firestore
        fetchProductsFromFirestore();

        return view;
    }

    // ✅ Fetch products from Firestore
    private void fetchProductsFromFirestore() {
        CollectionReference productsRef = db.collection("products");

        productsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId()); // Store Firestore document ID
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show()
                );
    }

    // ✅ Add product to the user's cart
    private void addProductToCart(Product product) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String productId = product.getBarcode();

            db.collection("users").document(userId).collection("cart")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // ✅ Product already in cart → increase quantity safely
                            Long currentQuantityLong = documentSnapshot.getLong("quantity");
                            int currentQuantity = (currentQuantityLong != null) ? currentQuantityLong.intValue() : 0;

                            db.collection("users").document(userId).collection("cart")
                                    .document(productId)
                                    .update("quantity", currentQuantity + 1)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                                        navController.navigate(R.id.action_productListFragment_to_home_page);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show()
                                    );

                        } else {
                            // ✅ Product not in cart → add with quantity 1
                            product.setQuantity(1); // Ensure `Product` has this method
                            db.collection("users").document(userId).collection("cart")
                                    .document(productId)
                                    .set(product)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Product added", Toast.LENGTH_SHORT).show();
                                        navController.navigate(R.id.action_productListFragment_to_home_page);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
