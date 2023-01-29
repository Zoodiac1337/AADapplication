package JsontoJava;

public class BarcodeObject {
 private String code;
 Product ProductObject;
 private float status;
 private String status_verbose;


 // Getter Methods 

 public String getCode() {
  return code;
 }

 public Product getProduct() {
  return ProductObject;
 }

 public float getStatus() {
  return status;
 }

 public String getStatus_verbose() {
  return status_verbose;
 }

 // Setter Methods 

 public void setCode(String code) {
  this.code = code;
 }

 public void setProduct(Product productObject) {
  this.ProductObject = productObject;
 }

 public void setStatus(float status) {
  this.status = status;
 }

 public void setStatus_verbose(String status_verbose) {
  this.status_verbose = status_verbose;
 }
}
