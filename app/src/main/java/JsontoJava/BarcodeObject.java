package JsontoJava;
public class BarcodeObject {
 private String code;
 private Product product;
 private long status;
 private String statusVerbose;

 public String getCode() { return code; }
 public void setCode(String value) { this.code = value; }

 public Product getProduct() { return product; }
 public void setProduct(Product value) { this.product = value; }

 public long getStatus() { return status; }
 public void setStatus(long value) { this.status = value; }

 public String getStatusVerbose() { return statusVerbose; }
 public void setStatusVerbose(String value) { this.statusVerbose = value; }
}

