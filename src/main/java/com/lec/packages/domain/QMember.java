package com.lec.packages.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1176967980L;

    public static final QMember member = new QMember("member1");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> CREATEDATE = _super.CREATEDATE;

    public final BooleanPath DELETE_FLAG = createBoolean("DELETE_FLAG");

    public final StringPath MEM_ADDRESS = createString("MEM_ADDRESS");

    public final StringPath MEM_BIRTHDAY = createString("MEM_BIRTHDAY");

    public final StringPath MEM_CLUB = createString("MEM_CLUB");

    public final StringPath MEM_EMAIL = createString("MEM_EMAIL");

    public final StringPath MEM_EXERCISE = createString("MEM_EXERCISE");

    public final BooleanPath MEM_GENDER = createBoolean("MEM_GENDER");

    public final StringPath MEM_ID = createString("MEM_ID");

    public final StringPath MEM_INTRODUCTION = createString("MEM_INTRODUCTION");

    public final BooleanPath MEM_ISMANAGER = createBoolean("MEM_ISMANAGER");

    public final StringPath MEM_NAME = createString("MEM_NAME");

    public final StringPath MEM_NICKNAME = createString("MEM_NICKNAME");

    public final StringPath MEM_PICTURE = createString("MEM_PICTURE");

    public final StringPath MEM_PW = createString("MEM_PW");

    public final BooleanPath MEM_SOCIAL = createBoolean("MEM_SOCIAL");

    public final StringPath MEM_TELL = createString("MEM_TELL");

    public final StringPath MEM_ZIPCODE = createString("MEM_ZIPCODE");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> MODIFYDATE = _super.MODIFYDATE;

    public final SetPath<MemberRole, EnumPath<MemberRole>> roleSet = this.<MemberRole, EnumPath<MemberRole>>createSet("roleSet", MemberRole.class, EnumPath.class, PathInits.DIRECT2);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

