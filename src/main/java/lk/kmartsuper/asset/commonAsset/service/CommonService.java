package lk.kmartsuper.asset.commonAsset.service;

import lk.kmartsuper.asset.supplier.entity.Supplier;
import lk.kmartsuper.asset.supplier.service.SupplierService;
import lk.kmartsuper.util.service.MakeAutoGenerateNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommonService {
    private final MakeAutoGenerateNumberService makeAutoGenerateNumberService;
    private final SupplierService supplierService;

    @Autowired
    public CommonService(MakeAutoGenerateNumberService makeAutoGenerateNumberService, SupplierService supplierService) {
        this.makeAutoGenerateNumberService = makeAutoGenerateNumberService;
        this.supplierService = supplierService;
    }

    public String supplierItemAndPurchaseOrderSearch(Supplier supplier, Model model, String htmlFileLocation) {
        List<Supplier> suppliers;
        if (supplier.getContactOne() != null) {
            String contactNumber = makeAutoGenerateNumberService.phoneNumberLengthValidator(supplier.getContactOne());
//all match with supplier contact number one
            supplier.setContactOne(contactNumber);
            supplier.setContactTwo(null);
            suppliers = new ArrayList<>(supplierService.search(supplier));
//all match with contact number two
            supplier.setContactOne(null);
            supplier.setContactTwo(contactNumber);
            suppliers.addAll(supplierService.search(supplier));

        } else {
            suppliers = supplierService.search(supplier);
        }
        if (supplier.getContactOne() != null) {
            suppliers = suppliers.stream()
                    .filter(supplier1 ->
                            supplier.getContactOne().equals(supplier1.getContactTwo()) ||
                                    supplier.getContactOne().equals(supplier1.getContactOne()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("searchAreaShow", false);
        if (suppliers.size() == 1) {
            model.addAttribute("supplierDetail", suppliers.get(0));
            model.addAttribute("supplierDetailShow", false);
            return htmlFileLocation;
        }
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("supplierDetailShow", true);
        return htmlFileLocation;
    }

    public void supplierItemAndPurchaseOrderView(Model model, Integer id) {
        model.addAttribute("searchAreaShow", false);
        model.addAttribute("supplierDetail", supplierService.findById(id));
        model.addAttribute("supplierDetailShow", false);
    }
}
