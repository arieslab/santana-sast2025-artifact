/*
 * Copyright 2017, 2018 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ibm.etcd.client.watch;

import com.ibm.etcd.api.ResponseHeader;

/**
 * Indicates the watch failed to be created
 */
public class WatchCreateException extends WatchCancelledException {

    private static final long serialVersionUID = -1917127569298295828L;

    WatchCreateException(ResponseHeader header, long watchId, String reason) {
        super("Watch creation failed", header, watchId, reason);
    }
}
