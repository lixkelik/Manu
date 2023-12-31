package id.co.manu.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import id.co.manu.R;
import id.co.manu.model.Factory;
import id.co.manu.viewmodel.FactoryViewModel;
import id.co.manu.views.adapter.FactoryAdapter;

public class ExploreFragment extends Fragment {

    private FactoryViewModel factoryViewModel;
    GridView gridView;
    FactoryAdapter factoryAdapter;
    ArrayList<Factory> arrayList = new ArrayList<>();
    EditText searchFilteredPabrik;
    AutoCompleteTextView autoCompleteDaerah, autoCompleteKategori;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        factoryViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(FactoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = view.findViewById(R.id.pabrikGrid);
        searchFilteredPabrik = view.findViewById(R.id.searchPabrik);
        autoCompleteDaerah = view.findViewById(R.id.autoCompleteDaerah);
        autoCompleteKategori = view.findViewById(R.id.autoCompleteKategori);

        factoryAdapter = new FactoryAdapter(new ArrayList<>(), requireContext());
        gridView.setAdapter(factoryAdapter);

        factoryViewModel.getAllFactory().observe(getActivity(), factoryList -> {
            if(arrayList != null){
                factoryAdapter.setFactoryList(factoryList);
            }
        });

        factoryViewModel.getDaerahList().observe(getViewLifecycleOwner(), daerahList -> {

            daerahList.add(0, "Semua");

            ArrayAdapter<String> autoCompleteDaerahAdapter = new ArrayAdapter<>(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    daerahList
            );
            autoCompleteDaerah.setAdapter(autoCompleteDaerahAdapter);
        });

        factoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categoryList -> {

            categoryList.add(0, "Semua");

            ArrayAdapter<String> autoCompleteCategoryAdapter = new ArrayAdapter<>(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    categoryList
            );
            autoCompleteKategori.setAdapter(autoCompleteCategoryAdapter);
        });
        searchFilteredPabrik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                factoryAdapter.getFilter().filter(editable.toString());
            }
        });
        autoCompleteDaerah.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDaerah = parent.getItemAtPosition(position).toString();
                if ("Semua".equals(selectedDaerah)) {
                    // Handle "Semua" selection, show all data
                    factoryAdapter.setFactoryList(factoryAdapter.getUnfilteredFactoryList());
                } else {
                    filterFactoryList(autoCompleteKategori.getText().toString(), selectedDaerah);
                }
            }
        });
        autoCompleteKategori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if ("Semua".equals(selectedCategory)) {
                    // Handle "Semua" selection, show all data
                    factoryAdapter.setFactoryList(factoryAdapter.getUnfilteredFactoryList());
                } else {
                    filterFactoryList(selectedCategory, autoCompleteDaerah.getText().toString());
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Factory selectedFactory = (Factory) factoryAdapter.getItem(i);
                if(selectedFactory != null){
                    Intent detailFactory = new Intent(requireContext(), DetailFactoryActivity.class);
                    detailFactory.putExtra("factory", selectedFactory);
                    startActivity(detailFactory);
                }
            }
        });
    }

    private void filterFactoryList(String selectedCategory, String selectedDaerah) {
        String nameFilter = searchFilteredPabrik.getText().toString();
        factoryAdapter.filterData(nameFilter, selectedDaerah, selectedCategory);
    }

}