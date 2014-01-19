import com.avaje.ebean.Ebean;
import controllers.ShopController;
import models.*;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import utils.EnumFormatter;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import play.mvc.Call;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 15.07.13
 * Time: 01:55
 */
public class Global extends GlobalSettings {

    public void onStart(Application app) {

        PlayAuthenticate.setResolver(new Resolver() {

            @Override
            public Call login() {
                // Your login page
                return controllers.routes.Signup.login();
            }

            @Override
            public Call afterAuth() {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                return controllers.routes.AccountController.profile();
            }

            @Override
            public Call afterLogout() {
                return controllers.routes.Signup.login();
            }

            @Override
            public Call auth(final String provider) {
                // You can provide your own authentication implementation,
                // however the default should be sufficient for most cases
                return com.feth.play.module.pa.controllers.routes.Authenticate.authenticate(provider);
            }

            @Override
            public Call askMerge() {
                return controllers.routes.AccountController.askMerge();
            }

            @Override
            public Call askLink() {
                return controllers.routes.AccountController.askLink();
            }

            @Override
            public Call onException(final AuthException e) {
                if (e instanceof AccessDeniedException) {
                    return controllers.routes.Signup
                            .oAuthDenied(((AccessDeniedException) e)
                                    .getProviderKey());
                }

                // more custom problem handling here...
                return super.onException(e);
            }
        });

        Formatters.register(BackendBasicModel.RowStatus.class, new EnumFormatter<>(BackendBasicModel.RowStatus.class));
        //Formatters.register(Address.Gender.class, new EnumFormatter<Address.Gender>(Address.Gender.class));


        initModelData();
    }

    private void initModelData() {

        if(Address.find.findRowCount() == 0) {
            Address.initSystemAccounts();
        }

        if(Language.find.findRowCount() == 0) {
            Language.initData();
        }

        if( SecurityRole.find.findRowCount() == 0 ) {
            SecurityRole.initData();
        }

        if(DocumentType.find.findRowCount() == 0) {
            DocumentType.initData();
        }

        if( AddressFunction.find.findRowCount() == 0 ) {
            AddressFunction.initData();
        }

        if( Currency.find.findRowCount() == 0) {
            importCurrency();
        }

        if(Region.find.findRowCount() == 0) {
            importCountries();
            importCountryAdministrativeRegions();
            importCountryZIPFormats();
            importCountryCallingCode();
            importCountryRegions();

        }

        if(Region.find.where().isNotNull("postalCode").findRowCount() == 0) {
            importSuissePLZ();
        }

        if(Unit.find.findRowCount() == 0) {
            Unit.initData();
        }

        if(LocalFile.find.findRowCount() == 0) {
            LocalFile.initTestData();
        }

        if(Website.find.findRowCount() == 0) {
            Website.initTestData();
        }

        importNBPralinen();
    }

    public void importNBPralinen() {
        if(ArticleCollection.find.findRowCount() == 0) {
            List<ArticleCollection> collectionList = new ArrayList<>();

            ArticleCollection pralinen = ArticleCollection.create(null, Language.getEnglish(), "Pralines");
            pralinen.setName(Language.getGerman(), "Pralinen");
            collectionList.add(pralinen);

            ArticleCollection risotto = ArticleCollection.create(null, Language.getEnglish(), "Risotto");
            risotto.setName(Language.getGerman(), "Risotto");
            collectionList.add(risotto);

            ArticleCollection marmeladen = ArticleCollection.create(null, Language.getEnglish(), "Jam");
            marmeladen.setName(Language.getGerman(), "Marmeladen");
            collectionList.add(marmeladen);

            ArticleCollection sirupe = ArticleCollection.create(null, Language.getEnglish(), "Syrups");
            sirupe.setName(Language.getGerman(), "Sirupe");
            collectionList.add(sirupe);

            ArticleCollection tee = ArticleCollection.create(null, Language.getEnglish(), "Tea");
            tee.setName(Language.getGerman(), "Tee");
            collectionList.add(tee);

            ArticleCollection geschenkkoerbe = ArticleCollection.create(null, Language.getEnglish(), "Hampers");
            geschenkkoerbe.setName(Language.getGerman(), "Geschenkkörbe");
            collectionList.add(geschenkkoerbe);

            ArticleCollection apfelringli = ArticleCollection.create(null, Language.getEnglish(), "Applerings");
            apfelringli.setName(Language.getGerman(), "Apfelringli");
            collectionList.add(apfelringli);

            ArticleCollection pflegeprodukte = ArticleCollection.create(null, Language.getEnglish(), "Care products");
            pflegeprodukte.setName(Language.getGerman(), "Pflegeprodukte");
            collectionList.add(pflegeprodukte);

            ArticleCollection pralinenMitAlk = ArticleCollection.create(pralinen, Language.getEnglish(), "with alcohol");
            pralinenMitAlk.setName(Language.getGerman(), "mit Alkohol");
            collectionList.add(pralinenMitAlk);

            ArticleCollection pralinenOhneAlk = ArticleCollection.create(pralinen, Language.getEnglish(), "without alcohol");
            pralinenOhneAlk.setName(Language.getGerman(), "ohne Alkohol");
            collectionList.add(pralinenOhneAlk);


            Ebean.save(collectionList);

            if(Article.find.findRowCount() == 0) {
                List<Article> articleList = new ArrayList<>();

                Article egg = new Article();
                egg.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                egg.articleCollections.add(pralinen);

                ArticleOption optionEgg = new ArticleOption();
                optionEgg.setTitle(Language.getEnglish(), "Easter egg");
                optionEgg.setTitle(Language.getGerman(), "Oster-Ei");
                optionEgg.setDescription(Language.getEnglish(), "Sweet chocolate egg filled with homemade pralines!");
                optionEgg.setDescription(Language.getGerman(), "Feines Schokoladen-Ei gefüllt mit hausgemachten Pralines!");
                optionEgg.number = "easter-egg";
                optionEgg.sellPrice = 9.90;
                optionEgg.sellPriceCurrency = Currency.findByCode("CHF");
                optionEgg.primaryPicture = LocalFile.find.byId(2L);
                egg.options.add(optionEgg);

                articleList.add(egg);

                Article praline1 = new Article();
                praline1.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                praline1.articleCollections.add(pralinen);

                ArticleOption optionPraline1 = new ArticleOption();
                optionPraline1.setTitle(Language.getEnglish(), "Cappucino Choco");
                optionPraline1.setTitle(Language.getGerman(), "Cappucino Choco");
                optionPraline1.setDescription(Language.getEnglish(), "Sweet chocolate with a taste of cappucino.");
                optionPraline1.setDescription(Language.getGerman(), "Feine Schokalade mit Cappucino Geschmack");
                optionPraline1.number = "praline-cap";
                optionPraline1.sellPrice = 2.50;
                optionPraline1.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline1.primaryPicture = LocalFile.find.byId(10L);
                praline1.options.add(optionPraline1);

                articleList.add(praline1);

                Article praline2 = new Article();
                praline2.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                praline2.articleCollections.add(pralinen);

                ArticleOption optionPraline2 = new ArticleOption();
                optionPraline2.setTitle(Language.getEnglish(), "Coffee");
                optionPraline2.setTitle(Language.getGerman(), "Coffee");
                optionPraline2.setDescription(Language.getEnglish(), "Sweet chocolate with a taste of coffee.");
                optionPraline2.setDescription(Language.getGerman(), "Feine Schokalade mit Kaffe Geschmack");
                optionPraline2.number = "praline-cof";
                optionPraline2.sellPrice = 2.30;
                optionPraline2.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline2.primaryPicture = LocalFile.find.byId(11L);
                praline2.options.add(optionPraline2);

                articleList.add(praline2);

                Article praline3 = new Article();
                praline3.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                praline3.articleCollections.add(pralinen);

                ArticleOption optionPraline3 = new ArticleOption();
                optionPraline3.setTitle(Language.getEnglish(), "Sweet Raspberry");
                optionPraline3.setTitle(Language.getGerman(), "Sweet Himbeere");
                optionPraline3.setDescription(Language.getEnglish(), "Chocolate praline with a taste of raspberry.");
                optionPraline3.setDescription(Language.getGerman(), "Feine Schokalade mit Himbeere Geschmack");
                optionPraline3.number = "praline-him";
                optionPraline3.sellPrice = 2.30;
                optionPraline3.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline3.primaryPicture = LocalFile.find.byId(12L);
                praline3.options.add(optionPraline3);

                articleList.add(praline3);

                Article praline4 = new Article();
                praline4.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                praline4.articleCollections.add(pralinen);

                ArticleOption optionPraline4 = new ArticleOption();
                optionPraline4.setTitle(Language.getEnglish(), "Honey");
                optionPraline4.setTitle(Language.getGerman(), "Honig");
                optionPraline4.setDescription(Language.getEnglish(), "Sweet chocolate with honey charge.");
                optionPraline4.setDescription(Language.getGerman(), "Feine Schokalade mit Honig Füllung");
                optionPraline4.number = "praline-hon";
                optionPraline4.sellPrice = 2.60;
                optionPraline4.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline4.primaryPicture = LocalFile.find.byId(13L);
                praline4.options.add(optionPraline4);

                articleList.add(praline4);

                Article praline5 = new Article();
                praline5.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                praline5.articleCollections.add(pralinen);

                ArticleOption optionPraline5 = new ArticleOption();
                optionPraline5.setTitle(Language.getEnglish(), "Truffe");
                optionPraline5.setTitle(Language.getGerman(), "Truffe");
                optionPraline5.setDescription(Language.getEnglish(), "Sweet chocolate with truffle.");
                optionPraline5.setDescription(Language.getGerman(), "Feine Schokalade mit Trüffel");
                optionPraline5.number = "praline-truf";
                optionPraline5.sellPrice = 2.90;
                optionPraline5.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline5.primaryPicture = LocalFile.find.byId(16L);
                praline5.options.add(optionPraline5);

                articleList.add(praline5);

                Article marmeladeErdbeere = new Article();
                marmeladeErdbeere.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                marmeladeErdbeere.articleCollections.add(marmeladen);

                ArticleOption optionPraline6 = new ArticleOption();
                optionPraline6.setTitle(Language.getEnglish(), "Strawberry Jam");
                optionPraline6.setTitle(Language.getGerman(), "Erdbeer Marmelade");
                optionPraline6.setDescription(Language.getEnglish(), "Homemade Strawberry jam");
                optionPraline6.setDescription(Language.getGerman(), "Hausgemachte Erdbeer Marmelade");
                optionPraline6.number = "marmelade-erd";
                optionPraline6.sellPrice = 4.90;
                optionPraline6.sellPriceCurrency = Currency.findByCode("CHF");
                optionPraline6.primaryPicture = LocalFile.find.byId(7L);
                marmeladeErdbeere.options.add(optionPraline6);

                articleList.add(marmeladeErdbeere);

                Article migrosSirup = new Article();
                migrosSirup.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
                migrosSirup.articleCollections.add(sirupe);

                ArticleOption optionMigrosSirup = new ArticleOption();
                optionMigrosSirup.setTitle(Language.getEnglish(), "Migros Sirupe");
                optionMigrosSirup.setTitle(Language.getGerman(), "Migros Sirup Siech Orange");
                optionMigrosSirup.setDescription(Language.getEnglish(), "the classic Migros sirupe with orange taste");
                optionMigrosSirup.setDescription(Language.getGerman(), "Dr Migros Sirup Siech haut");
                optionMigrosSirup.number = "sirup-ora";
                optionMigrosSirup.sellPrice = 3.20;
                optionMigrosSirup.sellPriceCurrency = Currency.findByCode("CHF");
                optionMigrosSirup.primaryPicture = LocalFile.find.byId(19L);
                migrosSirup.options.add(optionMigrosSirup);

                articleList.add(migrosSirup);

                Ebean.save(articleList);
            }
        }

        if(Address.find.findRowCount() < 3) {
            // id: 3 - Nicole Berger als ADMINISTRATOR
            Address userAdmin = new Address();
            userAdmin.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            userAdmin.preName = "Nicole";
            userAdmin.name = "Berger";
            userAdmin.gender = Address.Gender.FEMALE_PERSON;
            userAdmin.roles = new ArrayList<SecurityRole>();
            userAdmin.roles.add(SecurityRole.findByRoleName(SecurityRole.USER));
            userAdmin.roles.add(SecurityRole.findByRoleName(SecurityRole.ADMINISTRATOR));
            AddressLinkedAccount account = new AddressLinkedAccount();
            account.email = "nicole.berger@nb-pralinen.ch";
            account.rowStatus = BackendBasicModel.RowStatus.ACTIVE;
            account.emailValidated = true;
            account.providerKey = "password";
            account.providerUserId = "$2a$10$8lYwTKIVtt4X/NQl6AsBiOcWaAmWKWR//MZqSts5NR...LKcxZX5W"; // praline
            userAdmin.linkedAccounts = Collections.singletonList(account);
            userAdmin.save();
            userAdmin.saveManyToManyAssociations("roles");
        }
    }


    public void importCountries() {

        String csvFile = "./conf/initialData/GeoPC_Countries.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] country = line.split(";");

                if(country.length != 2) {
                    System.out.println("Zeile " + i + " ist ungültig: " + line);
                }

                for(int j = 0; j < country.length; j++) {
                    country[j] = country[j].replace("\"", "");
                }

                Region region = Region.getOrCreate(Region.RegionType.COUNTRY, null, country[0], country[1], country[1]);

                System.out.println("Country [code= " + region.regionCode + " , name=" + region.getName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCountryAdministrativeRegions() {

        String csvFile = "./conf/initialData/GeoPC_Adm_Regions.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if (i == 1) {
                    continue;
                }

                String[] country = line.split(";");

                if (country.length != 10) {
                    System.out.println("Zeile " + i + " ist ungültig: " + line);
                }

                for (int j = 0; j < country.length; j++) {
                    country[j] = country[j].replace("\"", "");
                }

                Region region = Region.getOrCreate(Region.RegionType.COUNTRY, null, country[0], country[1], country[1]);

                if (!country[2].isEmpty()) {
                    region.localStateName = country[2];
                }
                if (!country[4].isEmpty()) {
                    region.localDistrictName = country[4];
                }
                if (!country[6].isEmpty()) {
                    region.localSubdistrictName = country[6];
                }
                if (!country[8].isEmpty()) {
                    region.localCommuneName = country[8];
                }

                region.save();

                System.out.println("Administrative Region [code= " + region.regionCode + " , name=" + region.getName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCountryZIPFormats() {

        String csvFile = "./conf/initialData/GeoPC_ZIP_Formats.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] country = line.split(";");

                if(country.length != 4) {
                    System.out.println("Zeile " + i + " ist ungültig [length=" + country.length  + "]: " + line);
                    //continue;
                }

                for(int j = 0; j < country.length; j++) {
                    country[j] = country[j].replace("\"", "");
                }

                Region region = Region.getOrCreate(Region.RegionType.COUNTRY, null, country[0], country[1], country[1]);

                country[2] = country[2].replace("N/A", "");

                if(!country[2].isEmpty()) { region.postalCodeFormat = country[2]; }
                if(!country[3].isEmpty()) { region.postalCodeDenomination = country[3]; }

                region.save();

                System.out.println("Postal-Code [code= " + region.regionCode + " , name=" + region.getName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCountryCallingCode() {

        String csvFile = "./conf/initialData/GeoPC_Calling_codes.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] country = line.split(";");

                if(country.length != 3) {
                    System.out.println("Zeile " + i + " ist ungültig [length=" + country.length  + "]: " + line);
                    //continue;
                }

                for(int j = 0; j < country.length; j++) {
                    country[j] = country[j].replace("\"", "");
                }

                Region region = Region.getOrCreate(Region.RegionType.COUNTRY, null, country[0], country[1], country[1]);


                if(!country[2].isEmpty()) { region.telephonePrefix = country[2]; }

                region.save();

                System.out.println("Calling Code [code= " + region.regionCode + " , name=" + region.getName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCountryRegions() {

        String csvFile = "./conf/initialData/GeoPC_Regions_ISO3166_2.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] country = line.split(";");

                if(country.length != 6) {
                    System.out.println("Zeile " + i + " ist ungültig [length=" + country.length  + "]: " + line);
                    //continue;
                }

                for(int j = 0; j < country.length; j++) {
                    country[j] = country[j].replace("\"", "");
                }

                Region region = Region.getOrCreate(Region.RegionType.COUNTRY, null, country[0], country[1], country[1]);


                String statecode = country[2].substring(3).replace("-","");

                if(statecode.length() < 1) {
                    System.out.println("Zeile " + i + " ist der StateCode ungültig [statecode.length=" + statecode.length()  + "]: " + line);
                    //continue;
                }

                Region state = Region.getOrCreate(Region.RegionType.STATE, region, statecode, country[3], country[3]);

                //region.save();

                System.out.println("State Code [country=" + region.regionCode + ", code= " + state.regionCode + ", name=" + state.getName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCurrency() {

        String csvFile = "./conf/initialData/GeoPC_Currencies.csv";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] currencyElements = line.split(";");

                if(currencyElements.length != 4) {
                    System.out.println("Zeile " + i + " ist ungültig [length=" + currencyElements.length  + "]: " + line);
                    //continue;
                }

                for(int j = 0; j < currencyElements.length; j++) {
                    currencyElements[j] = currencyElements[j].replace("\"", "");
                }

                Currency currency = Currency.getOrCreate(currencyElements[0], currencyElements[1], currencyElements[2], currencyElements[3]);

                System.out.println("Currency [code= " + currency.code + " , name=" + currency.getLongName(Language.getEnglish()) + "]");

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void importSuissePLZ() {

        String csvFile = "./conf/initialData/plz_l_20131223.txt";
        BufferedReader bufferedReader = null;
        String line;
        Integer i = 0;

        try {
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while ((line = bufferedReader.readLine()) != null) {
                i += 1;
                if(i == 1) { continue; }

                String[] plzEntry = line.split("\t");

                if(plzEntry.length != 7) {
                    System.out.println("Zeile " + i + " ist ungültig [length=" + plzEntry.length  + "]: " + line);
                    continue;
                }

                for(int j = 0; j < plzEntry.length; j++) {
                    plzEntry[j] = plzEntry[j].replace("\"", "");
                }

                if(!( plzEntry[1].equals("20") || plzEntry[1].equals("10"))) {
                    // we want only import Domiziladdressen
                    System.out.println("Zeile " + i + " ist keine Domiziladresse!" + line);
                    continue;
                }


                Region switzerland = Region.findByRegionCode(null, "CH");
                if(switzerland == null) {
                    System.out.println("Zeile " + i + " hat folgenden Fehler: Land CH nicht gefunden!"  + line);
                    continue;
                }

                Region kanton = Region.findByRegionCode(switzerland, plzEntry[6]);

                Region commune = Region.getOrCreateCommune(kanton, plzEntry[2], plzEntry[5], plzEntry[5]);

                if(commune == null) {
                    System.out.println("Importiertfehler (commune=null) in Zeile " + line);
                }
                else {
                    System.out.println("PLZ Entry [plz=" + commune.postalCode + ", name=" + commune.getName(Language.getEnglish()) + "]");
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}