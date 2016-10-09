package komics.data

import java.util.*
import javax.persistence.*

/**
 * Created by ace on 2016/10/9.
 */
data class EClass(
        @Column(name = "e_id")
        override var id: String,
        @Column
        override var version: Long,
        override var created: Date,
        override var updated: Date,
        var name: String
) : Entity

@javax.persistence.Entity @Table(name = "user")
data class User(
        @Column(name = "uuid")
        override var id: String,
        @Column(name = "_version")
        override var version: Long,
        @Column
        override var created: Date,
        @Column
        override var updated: Date,
        @Column
        val name: String,
        @Transient
        val tmp: Long = 0,
        @OneToOne
        val o2o: User? = null,
        @OneToMany
        val o2m: User? = null,
        @ManyToOne
        val m2o: User? = null,
        @ManyToMany
        val m2m: User? = null
) : komics.data.Entity