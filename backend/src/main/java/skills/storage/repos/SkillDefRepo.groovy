package skills.storage.repos

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.lang.Nullable
import skills.storage.model.SkillDef
import skills.storage.model.SkillRelDef.RelationshipType

interface SkillDefRepo extends PagingAndSortingRepository<SkillDef, Integer> {

    List<SkillDef> findAllByProjectIdAndType(String id, SkillDef.ContainerType type)
    @Nullable
    SkillDef findByProjectIdAndSkillIdAndType(String id, String skillId, SkillDef.ContainerType type)

    @Query('SELECT s from SkillDef s where s.projectId = ?1 and s.version >= ?2 and s.type = ?3')
    List<SkillDef> findAllByProjectIdAndVersionAndType(String id, Integer version, SkillDef.ContainerType type)

    @Query(value = '''SELECT max(sdChild.displayOrder) from SkillDef sdParent, SkillRelDef srd, SkillDef sdChild
      where srd.parent=sdParent.id and srd.child=sdChild.id and 
      sdParent.projectId=?1 and sdParent.skillId=?2 and srd.type='RuleSetDefinition' ''' )
    @Nullable
    Integer calculateChildSkillsHighestDisplayOrder(String projectId, String skillId)

    @Query(value='''SELECT c 
        from SkillDef s, SkillRelDef r, SkillDef c 
        where s.id=r.parent and c.id = r.child and 
             s.projectId=?1 and s.skillId=?2 and c.displayOrder>?3 and r.type=?4
             order by c.displayOrder asc''')
    List<SkillDef> findNextSkillDefs(String projectId, String skillId, int afterDisplayOrder, RelationshipType relationshipType, Pageable pageable)

    @Query(value='''SELECT c 
        from SkillDef s, SkillRelDef r, SkillDef c 
        where s.id=r.parent and c.id = r.child and 
             s.projectId=?1 and s.skillId=?2 and c.displayOrder<?3 and r.type=?4
             order by c.displayOrder desc''')
    List<SkillDef> findPreviousSkillDefs(String projectId, String skillId, int beforeDisplayOrder, RelationshipType relationshipType, Pageable pageable)

    long countByProjectIdAndType(String projectId, SkillDef.ContainerType type)

    @Query(value='''select count(c) 
        from SkillRelDef r, SkillDef c 
        where r.parent=?1 and c.id = r.child and r.type=?2''')
    long countChildSkillsByIdAndRelationshipType(SkillDef parent, RelationshipType relationshipType)

    @Query(value='''select sum(c.totalPoints) 
        from SkillRelDef r, SkillDef c 
        where r.parent=?1 and c.id = r.child and r.type=?2''')
    long sumChildSkillsTotalPointsBySkillAndRelationshipType(SkillDef parent, RelationshipType relationshipType)

    @Query(value='''select count(distinct up.userId) 
        from SkillRelDef r, SkillDef c, UserPoints up 
        where up.projectId=?1  and r.parent=?2 
            and c.id = r.child and c.skillId = up.skillId 
            and r.type=?3''')
    long calculateDistinctUsersForChildSkills(String projectId, SkillDef parent, RelationshipType relationshipType)

    @Query(value = "SELECT COUNT(DISTINCT s.userId) from UserPoints s where s.projectId=?1 and s.skillId=?2")
    int calculateDistinctUsersForASingleSkill(String projectId, String skillId)

    boolean existsByProjectIdAndSkillIdAndType(String id, String skillId, SkillDef.ContainerType type)
    boolean existsByProjectIdAndNameAndType(String id, String name, SkillDef.ContainerType type)

    @Query('SELECT MAX (s.version) from SkillDef s where s.projectId=?1')
    Integer findMaxVersionByProjectId(String projectId)

    @Query(value='''SELECT count(c) 
        from SkillDef s, SkillRelDef r, SkillDef c 
        where 
            s.id = r.parent and c.id = r.child and 
            s.projectId=?1 and c.projectId=?1 and
            s.skillId=?2 and r.type=?3''')
    Integer countChildren(String projectId, String skillId, RelationshipType relationshipType)

    @Query("SELECT DISTINCT s.version from SkillDef s where s.projectId=?1 ORDER BY s.version ASC")
    List<Integer> getUniqueVersionList(String projectId)
}
