package com.aprimore.models.enuns;

public enum BladeType {



	STRAIGHT_CUT ("Corte reto"),
	CURVED_CUT ("Corte curvo"),
	WAVY_CUT ("Corte ondulado"),
	STRAIGHT_CREASE ("Vinco reto"),
	CURVED_CREASE ("Vinco curvo"),
	WAVY_CREASE ("Vinco ondulado"),
	STRAIGHT_PERFORATION ("Picote reto"),
	CUVED_PERFORATION ("Picote curvo"),
	LOCKED_PERFORATION ("Picote travado");

	private final String description;

    BladeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

	
}
