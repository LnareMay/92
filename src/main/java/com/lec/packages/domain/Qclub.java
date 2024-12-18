package com.lec.packages.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * Qclub is a Querydsl query type for club
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class Qclub extends EntityPathBase<club> {

    private static final long serialVersionUID = -1430728368L;

    public static final Qclub club = new Qclub("club");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath CLUB_ADDRESS = createString("CLUB_ADDRESS");

    public final StringPath CLUB_CODE = createString("CLUB_CODE");

    public final StringPath CLUB_EXERCISE = createString("CLUB_EXERCISE");

    public final NumberPath<Long> CLUB_IMAGE_1 = createNumber("CLUB_IMAGE_1", Long.class);

    public final NumberPath<Long> CLUB_IMAGE_2 = createNumber("CLUB_IMAGE_2", Long.class);

    public final NumberPath<Long> CLUB_IMAGE_3 = createNumber("CLUB_IMAGE_3", Long.class);

    public final NumberPath<Long> CLUB_IMAGE_4 = createNumber("CLUB_IMAGE_4", Long.class);

    public final StringPath CLUB_INTRODUCTION = createString("CLUB_INTRODUCTION");

    public final BooleanPath CLUB_ISPRIVATE = createBoolean("CLUB_ISPRIVATE");

    public final StringPath CLUB_NAME = createString("CLUB_NAME");

    public final StringPath CLUB_PW = createString("CLUB_PW");

    public final StringPath CLUB_THEME = createString("CLUB_THEME");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> CREATEDATE = _super.CREATEDATE;

    public final BooleanPath DELETE_FLAG = createBoolean("DELETE_FLAG");

    public final StringPath MEM_ID = createString("MEM_ID");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> MODIFYDATE = _super.MODIFYDATE;

    public Qclub(String variable) {
        super(club.class, forVariable(variable));
    }

    public Qclub(Path<? extends club> path) {
        super(path.getType(), path.getMetadata());
    }

    public Qclub(PathMetadata metadata) {
        super(club.class, metadata);
    }

}

