package models;

import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 17.07.13
 * Time: 21:12
 */
@Entity
public class Unit extends BackendBasicModel {

    public enum UnitType {
        @EnumValue("count") // one piece
        COUNT,

        @EnumValue("length") // meter
        LENGTH,

        @EnumValue("time") // second
        TIME,

        @EnumValue("mass") // gram
        MASS,

        @EnumValue("angle") // rad
        ANGLE,

        @EnumValue("temperature") // kelvin
        TEMPERATURE,

        @EnumValue("electric charge") // coulomb
        ELECTRIC_CHARGE,

        @EnumValue("luminous intensity") // candela
        LUMINOUS_INTENSITY,

        @EnumValue("digital") // bit
        DIGITALSIZE,
    }

    @Column(columnDefinition = "CHAR(20)")
    public UnitType type;

    @ManyToOne
    public Unit rootUnit;

    public Double factorToRootUnit;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unit")
    public List<UnitLanguage> languages;

    public void setName(Language language, String name) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(UnitLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.name = name;
                return;
            }
        }

        UnitLanguage newItem = new UnitLanguage();
        newItem.language = language;
        newItem.name = name;
        newItem.unit = this;

        this.languages.add(newItem);
    }

    public String getName(Language language) {
        for(UnitLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }

    public void setAbbrevation(Language language, String abbrevation) {

        if(language == null) {
            throw new IllegalArgumentException("Parameter language is null!");
        }

        if(this.languages == null) {
            this.languages = new ArrayList<>();
        }

        for(UnitLanguage item : this.languages) {
            if(item.language.equals(language)) {
                item.abbrevation = abbrevation;
                return;
            }
        }

        UnitLanguage newItem = new UnitLanguage();
        newItem.language = language;
        newItem.abbrevation = abbrevation;
        newItem.unit = this;

        this.languages.add(newItem);
    }

    public String getAbbrevation(Language language) {
        for(UnitLanguage item : this.languages) {
            if(item.language.equals(language)) {
                return item.name;
            }
        }

        return null;
    }

    public static final Finder<Long, Unit> find = new Finder<Long, Unit>(Long.class, Unit.class);

    public static Unit create(Language language, String name, String abbreviation, UnitType type, Unit rootUnit, Double factorToRootUnit) {

        Unit newUnit = new Unit();
        newUnit.setName(language, name);
        newUnit.setAbbrevation(language, abbreviation);
        newUnit.type = type;
        newUnit.rowStatus = RowStatus.ACTIVE;
        newUnit.rootUnit = rootUnit;
        newUnit.factorToRootUnit = factorToRootUnit;

        newUnit.save();

        return newUnit;
    }

    public static void initData() {
        // see http://unitsofmeasure.org/ucum.html

        // counting units
        Unit piece = create(Language.getEnglish(), "piece", "pce", UnitType.COUNT, null, 1.0);
        Unit pair = create(Language.getEnglish(), "pair", "pair", UnitType.COUNT, piece, 2.0);

        // length units
        Unit meter = create(Language.getEnglish(), "meter", "m", UnitType.LENGTH, null, 1.0);
        Unit kilometer = create(Language.getEnglish(), "kilometer", "km", UnitType.LENGTH, meter, 1000.0);
        Unit decimeter = create(Language.getEnglish(), "decimeter", "dm", UnitType.LENGTH, meter, 0.1);
        Unit centimeter = create(Language.getEnglish(), "centimeter", "cm", UnitType.LENGTH, meter, 0.01);
        Unit milimeter = create(Language.getEnglish(), "milimeter", "mm", UnitType.LENGTH, meter, 0.001);

        // time units
        Unit second = create(Language.getEnglish(), "second", "s", UnitType.TIME, null, 1.0);
        Unit miliseconds = create(Language.getEnglish(), "millisecond", "ms", UnitType.TIME, second, 0.001);

        // mass units
        Unit gram = create(Language.getEnglish(), "gram", "g", UnitType.MASS, null, 1.0);
        Unit kilogram = create(Language.getEnglish(), "kilogram", "kg", UnitType.MASS, gram, 1000.0);
        Unit ton = create(Language.getEnglish(), "ton", "t", UnitType.MASS, gram, 1000000.0);

    }
}
