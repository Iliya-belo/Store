package com.example.matala3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matala3.R;
import com.example.matala3.models.CartAdapter;
import com.example.matala3.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class home_page extends Fragment {

    private TextView tvWelcome, tvPhoneNumber;
    private Button btnLogout, btnAddProduct, btnRemoveProduct;
    private RecyclerView recyclerViewProducts;
    private CartAdapter cartAdapter;
    private List<Product> cartList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore ffdb;
    private NavController navController;
    private Product selectedProduct = null;

    public home_page() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        ffdb = FirebaseFirestore.getInstance();

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnRemoveProduct = view.findViewById(R.id.btnRemoveProduct);
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, product -> selectedProduct = product); // ✅ Pass click listener
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(cartAdapter);

        // ✅ Fix: Use NavHostFragment to find NavController
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            throw new IllegalStateException("NavController not found.");
        }

        // ✅ Fetch user data and cart
        fetchUserData();
        fetchCartItems();

        // ✅ Handle Logout Button Click
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.login2);
        });

        // ✅ Handle Add Product Button Click - Navigate to ProductListFragment
        btnAddProduct.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.productListFragment);
            } else {
                Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Handle Remove Product Button Click
        btnRemoveProduct.setOnClickListener(v -> {
            if (selectedProduct != null) {
                removeProductFromCart(selectedProduct);
            } else {
                Toast.makeText(getActivity(), "Select a product to remove", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // ✅ Fetch user data from Firestore and display it
    private void fetchUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            ffdb.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String phone = documentSnapshot.getString("phone");

                            tvWelcome.setText("Hello dear: " + username);
                            tvPhoneNumber.setText("Phone: " + phone);
                        } else {
                            Toast.makeText(getActivity(), "User  found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    // ✅ Fetch Shopping Cart Items from Firestore
    private void fetchCartItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            ffdb.collection("users").document(userId).collection("cart")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Product> newCartList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Product product = doc.toObject(Product.class);
                                newCartList.add(product);
                            }
                            cartAdapter.updateCartList(newCartList);
                        } else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // ✅ Remove product or decrease quantity
    private void removeProductFromCart(Product product) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference productRef = ffdb.collection("users").document(userId).collection("cart").document(product.getBarcode());

            if (product.getQuantity() > 1) {
                productRef.update("quantity", product.getQuantity() - 1);
            } else {
                productRef.delete();
            }
            fetchCartItems();
        }
    }
}
