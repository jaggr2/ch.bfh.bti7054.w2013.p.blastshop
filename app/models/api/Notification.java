package models.api;

import javax.persistence.Entity;

/**
 * Copyright 2013 blastbeat syndicate gmbh
 * Author: Roger Jaggi <roger.jaggi@blastbeatsyndicate.com>
 * Date: 02.08.13
 * Time: 15:16
 */
@Entity
public class Notification extends BackendBasicModel {
    String message;
}
