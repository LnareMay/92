package com.lec.packages.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * Qexercise_code_table is a Querydsl query type for exercise_code_table
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class Qexercise_code_table extends EntityPathBase<exercise_code_table> {

    private static final long serialVersionUID = -1294438423L;

    public static final Qexercise_code_table exercise_code_table = new Qexercise_code_table("exercise_code_table");

    public final StringPath EXERCISE_CODE = createString("EXERCISE_CODE");

    public final StringPath EXERCISE_NAME = createString("EXERCISE_NAME");

    public Qexercise_code_table(String variable) {
        super(exercise_code_table.class, forVariable(variable));
    }

    public Qexercise_code_table(Path<? extends exercise_code_table> path) {
        super(path.getType(), path.getMetadata());
    }

    public Qexercise_code_table(PathMetadata metadata) {
        super(exercise_code_table.class, metadata);
    }

}

