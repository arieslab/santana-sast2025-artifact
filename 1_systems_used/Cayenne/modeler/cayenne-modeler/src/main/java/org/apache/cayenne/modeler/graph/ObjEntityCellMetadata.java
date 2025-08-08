/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.modeler.graph;

import org.apache.cayenne.map.Attribute;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Entity;
import org.apache.cayenne.map.ObjEntity;

/**
 * Descriptor of ObjEntity Cell
 */
class ObjEntityCellMetadata extends EntityCellMetadata {
    ObjEntityCellMetadata(GraphBuilder builder, Entity entity) {
        super(builder, entity);
    }
    
    @Override
    public Entity fetchEntity() {
        for (DataMap dm : builder.getDataDomain().getDataMaps()) {
            ObjEntity objEntity = dm.getObjEntity(entityName);
            if (objEntity != null) {
                return objEntity;
            }
        }
        return null;
    }

    @Override
    protected boolean isPrimary(Attribute attr) {
        return false;
    }

}
