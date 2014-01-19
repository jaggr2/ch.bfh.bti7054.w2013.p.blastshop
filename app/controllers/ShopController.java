package controllers;

import actions.GlobalContextHelper;
import actions.UserAddress;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.apache.pdfbox.cos.COSString;
import play.i18n.Messages;
import play.mvc.Result;
import play.mvc.With;
import security.BackendUsernamePasswordAuthProvider;
import utils.ISO8601DateParser;
import views.html.*;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@With(GlobalContextHelper.class)
public class ShopController extends FrontendBasicController {

    public static final String USERFILEPATH = "." + File.separator + "userfiles" + File.separator;

    public static Result index() {
        return ok(website.render(GlobalContextHelper.getRootMenu(2)));
    }

    public static Result showCollection(Long collectionId) {

        ArticleCollection item = ArticleCollection.find.byId(collectionId);
        if(item == null) {
            return notFound();
        }

        return ok(collection.render(item));
    }

    public static Result showArticle(Long collectionId, Long articleId) {

        ArticleCollection item = ArticleCollection.find.byId(collectionId);
        if(item == null) {
            return notFound();
        }

        Article articleItem = Article.find.byId(articleId);
        if(articleItem == null) {
            return notFound();
        }

        return ok(article.render(item, articleItem));

    }

    public static Result getFile(Long fileId, String filename) {
        LocalFile fileItem = LocalFile.find.byId(fileId);

        if (fileItem != null) {
            File file = new File(USERFILEPATH + fileItem.id);
            if (file.exists() && file.isFile()) {
                response().setHeader("Content-Disposition", "attachment; filename=" + filename);

                if(fileItem.fileSize != null) {
                    response().setHeader("Content-Length", fileItem.fileSize.toString());
                }
                return ok(file);
            }
        }
        return notFound();
    }

    public static Result getSite(Long siteId, String siteName) {
        Website item = Website.find.byId(siteId);
        if(item == null) {
            notFound();
        }

        return ok(website.render(item));
    }

    public static Document getOrCreateBasketFromSession() {
        Document basket = null;

        if(session().containsKey("basketID")) {
            basket = Document.find.byId(Long.parseLong(session().get("basketID")));
        }

        if(basket == null) {
            basket = new Document();
            basket.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            basket.documentType = DocumentType.getShoppingCart();
            basket.save();
            session().put("basketID", basket.id.toString());
        }

        if(basket.clientAddress == null) {
            basket.clientAddress = UserAddress.current();
        }

        return basket;
    }

    public static Result showCheckout() {

        return ok(checkout.render(BackendUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result showHistory() {

        return ok(orderhistory.render(BackendUsernamePasswordAuthProvider.LOGIN_FORM,
                Document.find.where().eq("documentType", DocumentType.getClientOrder()).eq("clientAddress", UserAddress.current()).findList()));
    }

    public static Result setLanguage(String id) {
        Language language = Language.find.byId(id);
        if(language == null) {
            badRequest("Unbekannte Sprache");
        }

        session().remove(GlobalContextHelper.USER_LANGUAGE);
        session().put(GlobalContextHelper.USER_LANGUAGE, language.code);

        return ok();
    }

    public static Result getPDF(Long id) throws IOException, COSVisitorException {

        Document basket = Document.find.byId(id);

        if(basket == null) {
            return badRequest();
        }

        DecimalFormat dec2 = new DecimalFormat("#.##");
        DecimalFormat dec2fix = new DecimalFormat("#.00");

        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage( page );

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);


        List<String> test = new ArrayList<>();
        test.add((basket.clientAddress.preName != null ? basket.clientAddress.preName + " " : "") + basket.clientAddress.name);
        if(basket.clientAddress.addressline1 != null) { test.add(basket.clientAddress.addressline1); }
        if(basket.clientAddress.addressline2 != null) { test.add(basket.clientAddress.addressline2); }
        if(basket.clientAddress.city != null) { test.add(basket.clientAddress.city.postalCode + " " + basket.clientAddress.city.getName(Language.getEnglish())); }

        printMultipleLines(contentStream, test, 400, 700, PDType1Font.HELVETICA, 11);

        // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.moveTextPositionByAmount( 50, 530 );
        contentStream.drawString( Messages.get("blastshop.orderconfirmation.reporttitle", basket.id));
        contentStream.endText();

        String[][] table = new String[basket.rows.size() + 1][5];

        table[0][0] = Messages.get("blastshop.shoppingcart.product");
        table[0][1] = Messages.get("blastshop.shoppingcart.text");
        table[0][2] = Messages.get("blastshop.shoppingcart.price");
        table[0][3] = Messages.get("blastshop.shoppingcart.quantity");
        table[0][4] = Messages.get("blastshop.shoppingcart.subtotal");

        Double total = 0.0;

        for(int i = 1; i <= basket.rows.size(); i++) {
            DocumentRow row = basket.rows.get(i-1);

            table[i][0] = row.articleOption.number;
            table[i][1] = row.articleOption.getTitle(GlobalContextHelper.getUserLanguage());
            table[i][2] = dec2fix.format(row.price);
            table[i][3] = dec2.format(row.quantity);
            table[i][4] = dec2fix.format(((Double) (row.price * row.quantity)));

            total += row.price * row.quantity;
        }

        drawTable(page, contentStream, 500, 50, table);


        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.moveTextPositionByAmount(400, 470 - ((basket.rows.size() + 1) * 20));
        contentStream.drawString(Messages.get("blastshop.shoppingcart.grandtotal") + " " + dec2fix.format(total));
        contentStream.endText();

// Make sure that the content stream is closed:
        contentStream.close();

        // Save the results and ensure that the document is properly closed:
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        document.save(stream);
        document.close();

        return ok(stream.toByteArray()).as("application/pdf");
    }

    public static void drawTable(PDPage page, PDPageContentStream contentStream,
                                 float y, float margin,
                                 String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.findMediaBox().getWidth()-    (2*margin);
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin,nexty,margin+tableWidth,nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx,y,nextx,y-tableHeight);
            nextx += colWidth;
        }

        //now add the text

        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                contentStream.beginText();

                if(i == 0) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD,12);
                } else {
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                }

                contentStream.moveTextPositionByAmount(textx,texty);

                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }

    private static void printMultipleLines(
            PDPageContentStream contentStream,
            List<String> lines,
            float x,
            float y, PDFont font, float fontSize) throws IOException {
        if (lines.size() == 0) {
            return;
        }
        final int numberOfLines = lines.size();
        final float fontHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        contentStream.beginText();
        contentStream.setFont( font, fontSize );
        contentStream.appendRawCommands(fontHeight + " TL\n");
        contentStream.moveTextPositionByAmount(x, y);
        contentStream.drawString(lines.get(0));
        for (int i = 1; i < numberOfLines; i++) {
            contentStream.appendRawCommands(escapeString(lines.get(i)));
            contentStream.appendRawCommands(" \'\n");
        }
        contentStream.endText();
    }

    private static String escapeString(String text) throws IOException {
        try {
            COSString string = new COSString(text);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            string.writePDF(buffer);
            return new String(buffer.toByteArray(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // every JVM must know ISO-8859-1
            throw new RuntimeException(e);
        }
    }

    public static Result showOrderConfirmation() {

        Document confirmedBasket = getOrCreateBasketFromSession();

        if(!confirmedBasket.documentType.id.equals(DocumentType.getClientOrder().id)) {
            return badRequest("Die Bestellung muss abgeschlossen sein, bevor eine Bestätigung geladen werden kann. Status-ID:" + confirmedBasket.documentType.id.toString());
        }
        session().remove("basketID");

        Document newBasket = getOrCreateBasketFromSession();

        return ok(orderconfirmation.render(confirmedBasket));
    }

    public static Result updateBasket() throws JsonProcessingException, ParseException {
        if(request().body() == null) {
            return badRequest("body ist null");
        }

        if( request().body().asJson() == null) {
            return badRequest("body json ist null");
        }

        JsonNode body = request().body().asJson();

        if(body.isObject()) {
            Document basket = getOrCreateBasketFromSession();

            Address clientAddress = UserAddress.current();

            if(clientAddress != null && body.hasNonNull("clientAddress") && body.get("clientAddress").get("id").asLong() == clientAddress.id) {
                JsonNode addressNode = body.get("clientAddress");

                clientAddress.name = addressNode.get("name").asText();

                if( addressNode.hasNonNull("gender") ) { clientAddress.gender = Address.Gender.valueOf( addressNode.get("gender").asText() ); }

                if( addressNode.hasNonNull("preName") ) { clientAddress.preName = addressNode.get("preName").asText(); }

                if( addressNode.hasNonNull("addressline1") ) { clientAddress.addressline1 = addressNode.get("addressline1").asText(); }
                if( addressNode.hasNonNull("addressline2") ) { clientAddress.addressline2 = addressNode.get("addressline2").asText(); }


                if( addressNode.hasNonNull("birthdate")) {
                    ISO8601DateParser df = new ISO8601DateParser();

                    clientAddress.birthdate = df.parse(addressNode.get("birthdate").asText());
                }

                if( addressNode.hasNonNull("cityId")) {

                    Region city = Region.find.byId(addressNode.get("cityId").asLong());

                    clientAddress.city = city;
                }


                for(AddressContact contact : clientAddress.contacts) {
                    contact.delete();
                }

                if(addressNode.get("contacts").size() > 0) {


                    clientAddress.contacts.clear();

                    for(JsonNode contact : addressNode.get("contacts")) {


                        AddressContact addressContact = new AddressContact();

                        //EnumFormatter<AddressContact.ContactType> formatter = new EnumFormatter<>(AddressContact.ContactType.class);
                        //formatter.parse(contact.get("contactType").asText(), null);

                        addressContact.contactType = AddressContact.ContactType.valueOf( contact.get("contactType").asText() );


                        addressContact.value = contact.get("value").asText();

                        clientAddress.contacts.add(addressContact);


                    }

                }

                clientAddress.save();
            }

            basket.clientAddress = clientAddress;

            basket.documentType = DocumentType.find.byId(body.get("documentType").get("id").asLong());

            for(DocumentRow row : basket.rows) {
                row.delete();
            }

            if(body.get("rows").size() > 0) {


                basket.rows.clear();

                for(JsonNode theRow : body.get("rows")) {


                    DocumentRow documentRow = new DocumentRow();

                    ArticleOption articleOption = ArticleOption.find.byId(theRow.get("articleOption").get("id").asLong());
                    if(articleOption == null) {
                        return badRequest("unknown article option");
                    }
                    documentRow.articleOption = articleOption;
                    documentRow.price = articleOption.sellPrice;
                    documentRow.quantity = theRow.get("quantity").asDouble();

                    basket.rows.add(documentRow);
                }

            }

            basket.save();

            ObjectMapper objectMapper = new ObjectMapper();

            return ok(objectMapper.writeValueAsString(basket));
        }
        else {
            return badRequest("json body is not an object!");
        }
    }

    public static Result getBasket() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(getOrCreateBasketFromSession()));
    }

    public static Result getArticleOption(Long id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return ok(objectMapper.writeValueAsString(ArticleOption.find.byId(id)));
    }
}
