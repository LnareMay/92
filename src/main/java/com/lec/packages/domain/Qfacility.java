package com.lec.packages.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * Qfacility is a Querydsl query type for facility
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class Qfacility extends EntityPathBase<facility> {

    private static final long serialVersionUID = -403915203L;

    public static final Qfacility facility = new Qfacility("facility");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> CREATEDATE = _super.CREATEDATE;

    public final BooleanPath DELETE_FLAG = createBoolean("DELETE_FLAG");

    public final StringPath EXERCISE_CODE = createString("EXERCISE_CODE");

    public final StringPath FACILITY_ADDRESS = createString("FACILITY_ADDRESS");

    public final StringPath FACILITY_CODE = createString("FACILITY_CODE");

    public final StringPath FACILITY_DESCRIPTION = createString("FACILITY_DESCRIPTION");

    public final DateTimePath<java.time.LocalDateTime> FACILITY_END_TIME = createDateTime("FACILITY_END_TIME", java.time.LocalDateTime.class);

    public final NumberPath<Long> FACILITY_IMAGE_1 = createNumber("FACILITY_IMAGE_1", Long.class);

    public final NumberPath<Long> FACILITY_IMAGE_2 = createNumber("FACILITY_IMAGE_2", Long.class);

    public final NumberPath<Long> FACILITY_IMAGE_3 = createNumber("FACILITY_IMAGE_3", Long.class);

    public final NumberPath<Long> FACILITY_IMAGE_4 = createNumber("FACILITY_IMAGE_4", Long.class);

    public final BooleanPath FACILITY_ISONLYCLUB = createBoolean("FACILITY_ISONLYCLUB");

    public final StringPath FACILITY_NAME = createString("FACILITY_NAME");

    public final DateTimePath<java.time.LocalDateTime> FACILITY_START_TIME = createDateTime("FACILITY_START_TIME", java.time.LocalDateTime.class);

    public final StringPath FACILITY_ZIPCODE = createString("FACILITY_ZIPCODE");

    public final StringPath MEM_ID = createString("MEM_ID");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> MODIFYDATE = _super.MODIFYDATE;

    public final NumberPath<java.math.BigDecimal> PRICE = createNumber("PRICE", java.math.BigDecimal.class);

    public Qfacility(String variable) {
        super(facility.class, forVariable(variable));
    }

    public Qfacility(Path<? extends facility> path) {
        super(path.getType(), path.getMetadata());
    }

    public Qfacility(PathMetadata metadata) {
        super(facility.class, metadata);
    }

}

