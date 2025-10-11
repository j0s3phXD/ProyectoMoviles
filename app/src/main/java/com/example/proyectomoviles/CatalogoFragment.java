package com.example.proyectomoviles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectomoviles.databinding.FragmentCatalogoBinding;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatalogoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatalogoFragment extends Fragment {

    private FragmentCatalogoBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CatalogoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatalogoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CatalogoFragment newInstance(String param1, String param2) {
        CatalogoFragment fragment = new CatalogoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout con binding
        binding = FragmentCatalogoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Controlador de navegación
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Acción al presionar los CardView
        binding.card1.setOnClickListener(v -> navController.navigate(R.id.nav_detalle_producto));
        binding.card2.setOnClickListener(v -> navController.navigate(R.id.nav_detalle_producto));
        binding.card3.setOnClickListener(v -> navController.navigate(R.id.nav_detalle_producto));
        binding.card4.setOnClickListener(v -> navController.navigate(R.id.nav_detalle_producto));

        return view;
    }
}