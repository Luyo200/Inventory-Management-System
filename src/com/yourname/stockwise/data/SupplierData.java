package com.yourname.stockwise.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yourname.stockwise.model.Supplier;

public class SupplierData {
	private static final List<Supplier> suppliers = new ArrayList<>();

	
	public static void setSuppliers(List<Supplier> SupplierList) {
		suppliers.clear();
		suppliers.addAll(SupplierList);
	}

	public static List<Supplier> getAllSuppliers() {
		return Collections.unmodifiableList(suppliers);
	}

	public static void addSupplier(Supplier supplier) {
		suppliers.add(supplier);
	}

	public static void clearSuppliers() {
		suppliers.clear();
	}

}
