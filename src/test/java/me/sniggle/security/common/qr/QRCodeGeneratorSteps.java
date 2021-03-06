package me.sniggle.security.common.qr;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import me.sniggle.security.common.Parameter;
import me.sniggle.security.common.TwoFactorTypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by iulius on 29/06/15.
 */
public class QRCodeGeneratorSteps {

  private byte[] referenceFileSmall, referenceFileLarge;
  private QRCodeGenerator qrCodeGenerator;
  private Map<String, String> params;
  private byte[] actualImage;

  private static byte[] readFile(InputStream in) throws IOException {
    byte[] buffer = new byte[2048];
    int read = -1;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    while ((read = in.read(buffer)) != -1) {
      baos.write(buffer, 0, read);
    }
    return baos.toByteArray();
  }

  @Given("^I have the reference image ([a-z\\-\\.]+)$")
  public void i_have_the_reference_image_REFERENCE_FILE(String referenceFilename) throws Throwable {
    try( InputStream in = Files.newInputStream(Paths.get("src/test/resources/me/sniggle/security/common/qr/variant-small", referenceFilename)) ) {
      referenceFileSmall = readFile(in);
    }
    try( InputStream in = Files.newInputStream(Paths.get("src/test/resources/me/sniggle/security/common/qr/variant-large", referenceFilename))) {
      referenceFileLarge = readFile(in);
    }
  }

  @Given("^I have an instance of the QRCodeGenerator$")
  public void i_have_an_instance_of_the_QRCodeGenerator() throws Throwable {
    qrCodeGenerator = new QRCodeGenerator();
  }

  @Given("^I have the following parameters$")
  public void i_have_the_following_parameters(List<Map<String,String>> parameters) throws Throwable {
    params = parameters.get(0);
  }

  @When("^I generate the QR Code Image$")
  public void i_generate_the_QR_Code_Image() throws Throwable {
    actualImage = qrCodeGenerator.generateQRCode(
        TwoFactorTypes.valueOf(params.get("TYPE")),
        params.get("ISSUER"),
        params.get("ACCOUNT"),
        params.get("BASE32_SECRET"),
        new HashMap<Parameter, String>()
    );
  }

  public void assertFile(byte[] referenceFile) {
    for( int i = 0; i < referenceFile.length; i++ ) {
      assertEquals(referenceFile[i], actualImage[i]);
    }
  }

  @Then("^I expect the generated image to be identical to the reference image$")
  public void i_expect_the_generated_image_to_be_identical_to_the_reference_image() throws Throwable {
    assertTrue(referenceFileSmall.length == actualImage.length || referenceFileLarge.length == actualImage.length);
    if( referenceFileSmall.length == actualImage.length ) {
      assertFile(referenceFileSmall);
    } else {
      assertFile(referenceFileLarge);
    }
  }

}
