package com.imst.event.map.admin.db.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.imst.event.map.admin.db.ProjectionRepository;
import com.imst.event.map.admin.db.projections.UserLayerPermissionProjection;
import com.imst.event.map.admin.db.projections.UserLayerPermissionProjection2;
import com.imst.event.map.admin.vo.UserLayerPermissionItem;
import com.imst.event.map.hibernate.entity.Layer;
import com.imst.event.map.hibernate.entity.User;
import com.imst.event.map.hibernate.entity.UserLayerPermission;

@Repository
public interface UserLayerPermissionRepository extends ProjectionRepository<UserLayerPermission, Integer> {
	
	Page<UserLayerPermissionProjection> findAllProjectedBy(Pageable pageable);	
	UserLayerPermissionProjection findProjectedById(Integer id);
	

	UserLayerPermission findByUserIdAndLayerId(Integer userId, Integer layerId);
	
	
	List<UserLayerPermission> findAllByUserId(Integer userId);
	
	List<UserLayerPermissionProjection2> findAllProjectedByUser(User user);
	
//	@Query(value = "SELECT * " +
//					"FROM public.user_event_group_permission g join event_group eg on g.fk_event_group_id = eg.id;" +
//		
//					"select * from" +
//					"(" +
//					   "SELECT max(g.id), eg.fk_layer_id, g.fk_user_id" +
//							"FROM public.user_event_group_permission g join event_group eg on g.fk_event_group_id = eg.id group by g.fk_user_id, eg.fk_layer_id" +
//						"UNION ALL" +
//							"SELECT id, fk_layer_id, fk_user_id" +
//							"FROM public.user_layer_permission" +
//					") as u" +
//				"	where u.fk_layer_id = 5 and u.fk_user_id = 15", nativeQuery=true)
//	Page<UserLayerPermissionItem> findAllProjectedBy2(Pageable pageable, @Param("fk_layer_id") Integer layerId, @Param("fk_user_id") Integer userId);	

	
	
//	@Query(value = "SELECT * FROM public.user_layer_permission\r\n"
//			+ "ORDER BY id ASC ", nativeQuery=true)
//	List<UserLayerPermissionProjection> findAllByLayer();
	
	
//	@Query(value = "SELECT * FROM public.user_layer_permission u WHERE u.fk_layer_id = :layerId or u.fk_user_id = :userId" ,
//	   nativeQuery=true)
//    List<UserLayerPermissionProjection> findAllByLayer(Integer layerId, Integer userId);	
	
	
//	@Query(value = "SELECT * FROM public.user_layer_permission u WHERE u.fk_layer_id = :layerId or u.fk_user_id = :userId" ,
//			   nativeQuery=true)
//		    List<UserLayerPermissionProjection> deneme(Integer layerId, Integer userId);	

	Optional<Integer> findLayerIdByUserIdAndLayerId(Integer userId, Integer layerId);
	
	UserLayerPermission findByLayer(Layer layer);
	
	List<UserLayerPermission> findByLayerId(Integer layerId);
}
