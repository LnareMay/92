package com.lec.packages.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * Qmember_gd is a Querydsl query type for member_gd
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class Qmember_gd extends EntityPathBase<member_gd> {

    private static final long serialVersionUID = -943961016L;

    public static final Qmember_gd member_gd = new Qmember_gd("member_gd");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> CREATEDATE = _super.CREATEDATE;

    public final BooleanPath DELETE_FLAG = createBoolean("DELETE_FLAG");

    public final StringPath MEM_ADDRESS = createString("MEM_ADDRESS");

    public final StringPath MEM_ADDRESS_SET = createString("MEM_ADDRESS_SET");

    public final StringPath MEM_BIRTHDAY = createString("MEM_BIRTHDAY");

    public final StringPath MEM_EMAIL = createString("MEM_EMAIL");

    public final StringPath MEM_EXERCISE = createString("MEM_EXERCISE");

    public final BooleanPath MEM_GENDER = createBoolean("MEM_GENDER");

    public final StringPath MEM_ID = createString("MEM_ID");

    public final StringPath MEM_INTRODUCTION = createString("MEM_INTRODUCTION");

    public final BooleanPath MEM_ISMANAGER = createBoolean("MEM_ISMANAGER");

    public final StringPath MEM_NAME = createString("MEM_NAME");

    public final StringPath MEM_NICKNAME = createString("MEM_NICKNAME");

    public final NumberPath<Long> MEM_PICTURE = createNumber("MEM_PICTURE", Long.class);

    public final StringPath MEM_PW = createString("MEM_PW");

    public final StringPath MEM_TELL = createString("MEM_TELL");

    public final StringPath MEM_ZIPCODE = createString("MEM_ZIPCODE");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> MODIFYDATE = _super.MODIFYDATE;

    public Qmember_gd(String variable) {
        super(member_gd.class, forVariable(variable));
    }

    public Qmember_gd(Path<? extends member_gd> path) {
        super(path.getType(), path.getMetadata());
    }

    public Qmember_gd(PathMetadata metadata) {
        super(member_gd.class, metadata);
    }

}

