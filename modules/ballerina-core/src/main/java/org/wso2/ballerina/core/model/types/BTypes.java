/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.core.model.types;

import org.wso2.ballerina.core.model.SymbolName;
import org.wso2.ballerina.core.model.SymbolScope;

import static org.wso2.ballerina.core.model.types.TypeConstants.ARRAY_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.BOOLEAN_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.DOUBLE_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.INT_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.JSON_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.MAP_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.MESSAGE_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.STRING_TNAME;
import static org.wso2.ballerina.core.model.types.TypeConstants.XML_TNAME;

/**
 * This class contains various methods manipulate {@link BType}s in Ballerina.
 *
 * @since 0.8.0
 */
public class BTypes {
    public static  BType typeInt;
    public static  BType typeLong;
    public static  BType typeFloat;
    public static  BType typeDouble;
    public static  BType typeBoolean;
    public static  BType typeString;
    public static  BType typeXML;
    public static  BType typeJSON;
    public static  BType typeMessage;
    public static  BType typeMap;

    // TODO Temporary fix. Please remove this and refactor properly
    private static SymbolScope globalScope;

    private BTypes() {
    }

    public static void loadBuiltInTypes(SymbolScope globalScope) {
        typeInt = new BIntegerType(INT_TNAME, null, globalScope);
        typeDouble = new BDoubleType(DOUBLE_TNAME, null, globalScope);
        typeBoolean = new BBooleanType(BOOLEAN_TNAME, null, globalScope);
        typeString = new BStringType(STRING_TNAME, null, globalScope);
        typeXML = new BXMLType(XML_TNAME, null, globalScope);
        typeJSON = new BJSONType(JSON_TNAME, null, globalScope);
        typeMessage = new BMessageType(MESSAGE_TNAME, null, globalScope);
        typeMap = new BMapType(MAP_TNAME, null, globalScope);

        globalScope.define(typeInt.getSymbolName(), typeInt);
        globalScope.define(typeDouble.getSymbolName(), typeDouble);
        globalScope.define(typeBoolean.getSymbolName(), typeBoolean);
        globalScope.define(typeString.getSymbolName(), typeString);
        globalScope.define(typeXML.getSymbolName(), typeXML);
        globalScope.define(typeJSON.getSymbolName(), typeJSON);
        globalScope.define(typeMessage.getSymbolName(), typeMessage);
        globalScope.define(typeMap.getSymbolName(), typeMap);

        BTypes.globalScope = globalScope;
    }

    public static BArrayType getArrayType(String elementTypeName) {
        String arrayTypeName = elementTypeName + ARRAY_TNAME;

        return (BArrayType) globalScope.resolve(new SymbolName(arrayTypeName));
//        BArrayType type = BType.getType(arrayTypeName);
//        if (type == null) {
//            type = new BArrayType(arrayTypeName, elementTypeName);
//        }

//        return type;
    }

//    public static void addConnectorType(String connectorName) {
//        new BConnectorType(connectorName);
//    }

    public static boolean isValueType(BType type) {
        if (type == BTypes.typeInt ||
                type == BTypes.typeString ||
                type == BTypes.typeLong ||
                type == BTypes.typeFloat ||
                type == BTypes.typeDouble ||
                type == BTypes.typeBoolean) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BType> T getType(String typeName) {
        return (T) globalScope.resolve(new SymbolName(typeName));
//        return BType.getType(typeName);
    }
    
//    public static void addStructType(String structName) {
//        new BStructType(structName);
//    }
}
